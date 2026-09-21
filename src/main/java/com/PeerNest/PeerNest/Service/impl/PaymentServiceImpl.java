package com.PeerNest.PeerNest.Service.impl;

import com.PeerNest.PeerNest.Dto.PaymentOrderResponse;
import com.PeerNest.PeerNest.Dto.PaymentVerificationRequest;
import com.PeerNest.PeerNest.Dto.PaymentVerificationResponse;
import com.PeerNest.PeerNest.Entity.Course;
import com.PeerNest.PeerNest.Entity.Enrollment;
import com.PeerNest.PeerNest.Entity.User;
import com.PeerNest.PeerNest.Repository.CourseRepository;
import com.PeerNest.PeerNest.Repository.EnrollmentRepository;
import com.PeerNest.PeerNest.Repository.UserRepository;
import com.PeerNest.PeerNest.Service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RazorpayClient razorpayClient;

    private final CourseRepository courseRepository;

    private final EnrollmentRepository enrollmentRepository;

    private final UserRepository userRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;


    // =========================================================
    // CREATE RAZORPAY ORDER
    // =========================================================

    @Override
    public PaymentOrderResponse createOrder(
            Long courseId,
            String studentEmail
    ) {

        // Find course
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course not found")
                );


        // Check course status
        if (course.getStatus() == null ||
                !course.getStatus().name().equals("PUBLISHED")) {

            throw new RuntimeException(
                    "Only published courses can be purchased"
            );
        }


        // Free course should not go through Razorpay
        if (course.isFree()) {

            throw new RuntimeException(
                    "This course is free. Please enroll directly."
            );
        }


        // Check price
        if (course.getPrice() <= 0) {

            throw new RuntimeException(
                    "Invalid course price"
            );
        }


        // Check student
        User student = userRepository
                .findByEmail(studentEmail)
                .orElseThrow(() ->
                        new RuntimeException("Student not found")
                );


        // Check if already enrolled
        boolean alreadyEnrolled =
                enrollmentRepository
                        .existsByStudentIdAndCourseId(
                                student.getId(),
                                courseId
                        );

        if (alreadyEnrolled) {

            throw new RuntimeException(
                    "You are already enrolled in this course"
            );
        }


        try {

            /*
             * Razorpay amount is always sent in paise.
             *
             * Example:
             *
             * ₹499 = 49900 paise
             */
            long amountInPaise =
                    Math.round(course.getPrice() * 100);


            JSONObject orderRequest =
                    new JSONObject();

            orderRequest.put(
                    "amount",
                    amountInPaise
            );

            orderRequest.put(
                    "currency",
                    "INR"
            );

            orderRequest.put(
                    "receipt",
                    "peernest_course_" + courseId + "_" +
                            System.currentTimeMillis()
            );


            // Create Razorpay order
            Order razorpayOrder =
                    razorpayClient.orders.create(
                            orderRequest
                    );


            String orderId =
                    razorpayOrder.get("id");


            return new PaymentOrderResponse(
                    orderId,
                    course.getId(),
                    course.getTitle(),
                    amountInPaise,
                    "INR",
                    razorpayKeyId
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to create Razorpay order: "
                            + e.getMessage(),
                    e
            );
        }
    }


    // =========================================================
    // VERIFY RAZORPAY PAYMENT
    // =========================================================

    @Override
    public PaymentVerificationResponse verifyPayment(
            PaymentVerificationRequest request,
            String studentEmail
    ) {

        try {

            // -------------------------------------------------
            // Validate request
            // -------------------------------------------------

            if (request == null) {

                return new PaymentVerificationResponse(
                        false,
                        "Invalid payment request",
                        null
                );
            }


            if (request.getCourseId() == null ||
                    request.getRazorpayOrderId() == null ||
                    request.getRazorpayPaymentId() == null ||
                    request.getRazorpaySignature() == null) {

                return new PaymentVerificationResponse(
                        false,
                        "Missing payment information",
                        null
                );
            }


            // -------------------------------------------------
            // Find course
            // -------------------------------------------------

            Course course =
                    courseRepository
                            .findById(request.getCourseId())
                            .orElse(null);


            if (course == null) {

                return new PaymentVerificationResponse(
                        false,
                        "Course not found",
                        null
                );
            }


            // -------------------------------------------------
            // Check course status
            // -------------------------------------------------

            if (course.getStatus() == null ||
                    !course.getStatus()
                            .name()
                            .equals("PUBLISHED")) {

                return new PaymentVerificationResponse(
                        false,
                        "Course is not available for purchase",
                        null
                );
            }


            // -------------------------------------------------
            // Check paid course
            // -------------------------------------------------

            if (course.isFree()) {

                return new PaymentVerificationResponse(
                        false,
                        "This course is free. Please enroll directly.",
                        null
                );
            }


            // -------------------------------------------------
            // Find student
            // -------------------------------------------------

            User student =
                    userRepository
                            .findByEmail(studentEmail)
                            .orElse(null);


            if (student == null) {

                return new PaymentVerificationResponse(
                        false,
                        "Student not found",
                        null
                );
            }


            // -------------------------------------------------
            // Check duplicate enrollment
            // -------------------------------------------------

            boolean alreadyEnrolled =
                    enrollmentRepository
                            .existsByStudentIdAndCourseId(
                                    student.getId(),
                                    course.getId()
                            );


            if (alreadyEnrolled) {

                return new PaymentVerificationResponse(
                        true,
                        "You are already enrolled in this course",
                        null
                );
            }


            // -------------------------------------------------
            // Prepare Razorpay signature data
            // -------------------------------------------------

            JSONObject attributes =
                    new JSONObject();

            attributes.put(
                    "razorpay_order_id",
                    request.getRazorpayOrderId()
            );

            attributes.put(
                    "razorpay_payment_id",
                    request.getRazorpayPaymentId()
            );

            attributes.put(
                    "razorpay_signature",
                    request.getRazorpaySignature()
            );


            // -------------------------------------------------
            // Verify Razorpay signature
            // -------------------------------------------------

            boolean signatureValid =
                    Utils.verifyPaymentSignature(
                            attributes,
                            razorpayKeySecret
                    );


            if (!signatureValid) {

                return new PaymentVerificationResponse(
                        false,
                        "Payment verification failed",
                        null
                );
            }


            // -------------------------------------------------
            // Create enrollment
            // -------------------------------------------------

            Enrollment enrollment =
                    new Enrollment();

            enrollment.setStudent(student);

            enrollment.setCourse(course);

            enrollment.setEnrolledAt(
                    LocalDateTime.now()
            );


            Enrollment savedEnrollment =
                    enrollmentRepository.save(
                            enrollment
                    );


            // -------------------------------------------------
            // Success
            // -------------------------------------------------

            return new PaymentVerificationResponse(
                    true,
                    "Payment successful. You are now enrolled.",
                    savedEnrollment.getId()
            );


        } catch (Exception e) {

            return new PaymentVerificationResponse(
                    false,
                    "Payment verification failed: "
                            + e.getMessage(),
                    null
            );
        }
    }
}