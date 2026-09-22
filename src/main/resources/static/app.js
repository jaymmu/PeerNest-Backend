// =====================================================
// PeerNest Frontend - app.js
// =====================================================

const API = "http://localhost:8081";

// =====================================================
// AUTH DATA
// =====================================================

let token = localStorage.getItem("peernest_token") || "";
let role = localStorage.getItem("peernest_role") || "";
let email = localStorage.getItem("peernest_email") || "";
let name = localStorage.getItem("peernest_name") || "";

let uploadedVideoUrl = "";


// =====================================================
// AUTH HEADERS
// =====================================================

function authHeaders(json = true) {

    const headers = {};

    if (json) {
        headers["Content-Type"] = "application/json";
    }

    if (token) {
        headers["Authorization"] = "Bearer " + token;
    }

    return headers;
}


// =====================================================
// API HELPER
// =====================================================

async function api(path, options = {}) {

    try {

        const response = await fetch(API + path, options);

        const text = await response.text();

        let data;

        try {
            data = text ? JSON.parse(text) : null;
        } catch (error) {
            data = text;
        }

        if (!response.ok) {

            console.error("API ERROR:", response.status, data);

            throw new Error(
                typeof data === "string"
                    ? data
                    : data?.message || "Something went wrong"
            );
        }

        return data;

    } catch (error) {

        console.error("REQUEST ERROR:", error);

        throw error;
    }
}


// =====================================================
// LOGIN
// =====================================================

async function loginUser(event) {

    if (event) {
        event.preventDefault();
    }

    const emailInput =
        document.getElementById("loginEmail");

    const passwordInput =
        document.getElementById("loginPassword");

    if (!emailInput || !passwordInput) {
        console.error("Login fields not found");
        return;
    }

    const loginData = {
        email: emailInput.value.trim(),
        password: passwordInput.value
    };

    try {

        const response = await api(
            "/api/auth/login",
            {
                method: "POST",
                headers: authHeaders(true),
                body: JSON.stringify(loginData)
            }
        );

        console.log("LOGIN SUCCESS:", response);

        token = response.token || "";
        role = response.role || "";
        email = response.email || loginData.email;
        name = response.name || "";

        localStorage.setItem(
            "peernest_token",
            token
        );

        localStorage.setItem(
            "peernest_role",
            role
        );

        localStorage.setItem(
            "peernest_email",
            email
        );

        localStorage.setItem(
            "peernest_name",
            name
        );

        alert("Login successful!");

        window.location.href = "index.html";

    } catch (error) {

        alert(
            "Login failed: " + error.message
        );
    }
}


// =====================================================
// LOGOUT
// =====================================================

function logoutUser() {

    localStorage.removeItem("peernest_token");
    localStorage.removeItem("peernest_role");
    localStorage.removeItem("peernest_email");
    localStorage.removeItem("peernest_name");

    token = "";
    role = "";
    email = "";
    name = "";

    window.location.href = "index.html";
}


// =====================================================
// GET COURSES
// =====================================================

async function getCourses() {

    try {

        const courses = await api(
            "/api/courses",
            {
                method: "GET",
                headers: authHeaders(false)
            }
        );

        console.log("COURSES:", courses);

        return courses;

    } catch (error) {

        console.error(
            "Unable to load courses:",
            error
        );

        return [];
    }
}


// =====================================================
// GET COURSE BY ID
// =====================================================

async function getCourse(courseId) {

    try {

        const course = await api(
            `/api/courses/${courseId}`,
            {
                method: "GET",
                headers: authHeaders(false)
            }
        );

        console.log("COURSE:", course);

        return course;

    } catch (error) {

        console.error(
            "Unable to load course:",
            error
        );

        return null;
    }
}


// =====================================================
// SEARCH COURSES
// =====================================================

async function searchCourses(keyword) {

    try {

        const courses = await api(
            `/api/courses/search?keyword=${encodeURIComponent(keyword)}`,
            {
                method: "GET",
                headers: authHeaders(false)
            }
        );

        console.log("SEARCH RESULTS:", courses);

        return courses;

    } catch (error) {

        console.error(
            "Course search failed:",
            error
        );

        return [];
    }
}


// =====================================================
// STUDENT ENROLLMENT
// =====================================================

async function enrollFreeCourse(courseId) {

    if (!token) {

        alert("Please login first.");

        return;
    }

    try {

        const response = await api(
            `/api/student/enrollments/${courseId}`,
            {
                method: "POST",
                headers: authHeaders(false)
            }
        );

        console.log(
            "ENROLLMENT SUCCESS:",
            response
        );

        alert("Successfully enrolled!");

        return response;

    } catch (error) {

        alert(
            "Enrollment failed: " +
            error.message
        );
    }
}


// =====================================================
// GET MY ENROLLMENTS
// =====================================================

async function getMyEnrollments() {

    if (!token) {

        console.warn(
            "No token found. User is not logged in."
        );

        return [];
    }

    try {

        const enrollments = await api(
            "/api/student/enrollments",
            {
                method: "GET",
                headers: authHeaders(false)
            }
        );

        console.log(
            "MY ENROLLMENTS:",
            enrollments
        );

        return enrollments;

    } catch (error) {

        console.error(
            "Unable to load enrollments:",
            error
        );

        return [];
    }
}


// =====================================================
// ⭐ STUDENT SECTIONS
// =====================================================

async function getStudentSections(courseId) {

    if (!token) {

        console.warn(
            "No JWT token available."
        );

        alert("Please login first.");

        return [];
    }

    try {

        console.log(
            "Loading sections for course:",
            courseId
        );

        console.log(
            "JWT available:",
            !!token
        );

        const sections = await api(
            `/api/student/sections/course/${courseId}`,
            {
                method: "GET",

                // ⭐ THIS IS THE IMPORTANT PART
                headers: authHeaders(false)
            }
        );

        console.log(
            "STUDENT SECTIONS:",
            sections
        );

        return sections;

    } catch (error) {

        console.error(
            "Unable to load student sections:",
            error
        );

        return [];
    }
}


// =====================================================
// GET LECTURES FOR SECTION
// =====================================================

async function getStudentLectures(sectionId) {

    try {

        const lectures = await api(
            `/api/student/lectures/section/${sectionId}`,
            {
                method: "GET",
                headers: authHeaders(false)
            }
        );

        console.log(
            "STUDENT LECTURES:",
            lectures
        );

        return lectures;

    } catch (error) {

        console.error(
            "Unable to load lectures:",
            error
        );

        return [];
    }
}


// =====================================================
// LOAD COURSE CONTENT
// =====================================================

async function loadCourseContent(courseId) {

    console.log(
        "Loading course content:",
        courseId
    );

    const sections =
        await getStudentSections(courseId);

    if (!sections || sections.length === 0) {

        console.log(
            "No sections found."
        );

        return;
    }

    for (const section of sections) {

        console.log(
            "Section:",
            section
        );

        const lectures =
            await getStudentLectures(
                section.id
            );

        console.log(
            `Lectures for section ${section.id}:`,
            lectures
        );
    }

    return sections;
}


// =====================================================
// CREATE RAZORPAY ORDER
// =====================================================

async function createPaymentOrder(courseId) {

    if (!token) {

        alert("Please login first.");

        return null;
    }

    try {

        const order = await api(
            `/api/payment/create-order/${courseId}`,
            {
                method: "POST",
                headers: authHeaders(false)
            }
        );

        console.log(
            "RAZORPAY ORDER:",
            order
        );

        return order;

    } catch (error) {

        console.error(
            "Payment order failed:",
            error
        );

        alert(
            "Unable to create payment order: " +
            error.message
        );

        return null;
    }
}


// =====================================================
// VERIFY PAYMENT
// =====================================================

async function verifyPayment(
    courseId,
    razorpayOrderId,
    razorpayPaymentId,
    razorpaySignature
) {

    if (!token) {

        alert("Please login first.");

        return null;
    }

    const paymentData = {

        courseId: courseId,

        razorpayOrderId:
            razorpayOrderId,

        razorpayPaymentId:
            razorpayPaymentId,

        razorpaySignature:
            razorpaySignature
    };

    try {

        const response = await api(
            "/api/payment/verify",
            {
                method: "POST",

                headers:
                    authHeaders(true),

                body:
                    JSON.stringify(paymentData)
            }
        );

        console.log(
            "PAYMENT VERIFICATION:",
            response
        );

        if (response.success) {

            alert(
                "Payment successful! You are enrolled."
            );

        } else {

            alert(
                response.message ||
                "Payment verification failed."
            );
        }

        return response;

    } catch (error) {

        console.error(
            "Payment verification error:",
            error
        );

        alert(
            "Payment verification failed: " +
            error.message
        );

        return null;
    }
}


// =====================================================
// RAZORPAY CHECKOUT
// =====================================================

async function buyCourse(courseId) {

    if (!token) {

        alert(
            "Please login before purchasing a course."
        );

        return;
    }

    try {

        const order =
            await createPaymentOrder(
                courseId
            );

        if (!order) {
            return;
        }

        const options = {

            key: order.keyId,

            amount: order.amount,

            currency:
                order.currency || "INR",

            name: "PeerNest",

            description:
                order.courseTitle,

            order_id:
                order.orderId,

            handler: async function (response) {

                console.log(
                    "RAZORPAY RESPONSE:",
                    response
                );

                await verifyPayment(

                    courseId,

                    response.razorpay_order_id,

                    response.razorpay_payment_id,

                    response.razorpay_signature
                );
            },

            prefill: {

                name: name,

                email: email
            },

            theme: {

                color: "#4f46e5"
            }
        };

        if (
            typeof Razorpay ===
            "undefined"
        ) {

            alert(
                "Razorpay SDK is not loaded."
            );

            return;
        }

        const razorpay =
            new Razorpay(options);

        razorpay.open();

    } catch (error) {

        console.error(
            "Checkout error:",
            error
        );
    }
}


// =====================================================
// CLOUDINARY VIDEO UPLOAD
// =====================================================

async function uploadVideo() {

    if (!token) {

        alert("Please login first.");

        return;
    }

    const fileInput =
        document.getElementById(
            "videoFile"
        );

    if (!fileInput) {

        console.error(
            "videoFile input not found"
        );

        return;
    }

    const file =
        fileInput.files[0];

    if (!file) {

        alert(
            "Please select a video."
        );

        return;
    }

    const formData =
        new FormData();

    formData.append(
        "file",
        file
    );

    try {

        const response =
            await fetch(
                API +
                "/api/instructor/upload/video",
                {
                    method: "POST",

                    headers: {

                        "Authorization":
                            "Bearer " +
                            token
                    },

                    body:
                        formData
                }
            );

        const data =
            await response.json();

        if (!response.ok) {

            throw new Error(
                data?.message ||
                "Video upload failed"
            );
        }

        uploadedVideoUrl =
            data.videoUrl ||
            data.url ||
            data.secure_url ||
            data;

        console.log(
            "VIDEO URL:",
            uploadedVideoUrl
        );

        alert(
            "Video uploaded successfully!"
        );

        return uploadedVideoUrl;

    } catch (error) {

        console.error(
            "Video upload error:",
            error
        );

        alert(
            "Video upload failed: " +
            error.message
        );
    }
}


// =====================================================
// CREATE LECTURE
// =====================================================

async function createLecture() {

    if (!token) {

        alert("Please login first.");

        return;
    }

    const sectionId =
        document.getElementById(
            "lectureSectionId"
        )?.value;

    const title =
        document.getElementById(
            "lectureTitle"
        )?.value;

    const description =
        document.getElementById(
            "lectureDescription"
        )?.value;

    const duration =
        document.getElementById(
            "lectureDuration"
        )?.value;

    const lectureOrder =
        document.getElementById(
            "lectureOrder"
        )?.value;

    const freePreview =
        document.getElementById(
            "freePreview"
        )?.checked || false;

    if (!sectionId) {

        alert(
            "Section ID is required."
        );

        return;
    }

    if (!title) {

        alert(
            "Lecture title is required."
        );

        return;
    }

    if (!uploadedVideoUrl) {

        alert(
            "Please upload a video first."
        );

        return;
    }

    const lectureData = {

        sectionId:
            Number(sectionId),

        title:
            title,

        description:
            description || "",

        videoUrl:
            uploadedVideoUrl,

        durationInMinutes:
            duration
                ? Number(duration)
                : 0,

        lectureOrder:
            lectureOrder
                ? Number(lectureOrder)
                : 1,

        freePreview:
            freePreview
    };

    try {

        const response =
            await api(
                "/api/instructor/lectures",
                {
                    method: "POST",

                    headers:
                        authHeaders(true),

                    body:
                        JSON.stringify(
                            lectureData
                        )
                }
            );

        console.log(
            "LECTURE CREATED:",
            response
        );

        alert(
            "Lecture created successfully!"
        );

        uploadedVideoUrl = "";

        return response;

    } catch (error) {

        console.error(
            "Lecture creation failed:",
            error
        );

        alert(
            "Lecture creation failed: " +
            error.message
        );
    }
}


// =====================================================
// CHECK LOGIN
// =====================================================

function isLoggedIn() {

    return !!token;
}


// =====================================================
// CURRENT USER
// =====================================================

function getCurrentUser() {

    return {

        token: token,

        role: role,

        email: email,

        name: name
    };
}


// =====================================================
// PAGE INITIALIZATION
// =====================================================

document.addEventListener(
    "DOMContentLoaded",
    function () {

        console.log(
            "PeerNest frontend loaded."
        );

        console.log(
            "API:",
            API
        );

        console.log(
            "Logged in:",
            !!token
        );

        console.log(
            "Role:",
            role
        );

        console.log(
            "Email:",
            email
        );
    }
);