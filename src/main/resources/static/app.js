// ============================================================
// PEERNEST - API CONFIGURATION
// ============================================================

const API = "http://localhost:8081";

let token = localStorage.getItem("peernest_token") || "";
let role = localStorage.getItem("peernest_role") || "";
let email = localStorage.getItem("peernest_email") || "";

// This stores the Cloudinary URL after video upload
let uploadedVideoUrl = "";


// ============================================================
// AUTH HEADERS
// ============================================================

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


// ============================================================
// GENERIC API FUNCTION
// ============================================================

async function api(path, options = {}) {

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
}


// ============================================================
// VIDEO UPLOAD TO CLOUDINARY
// ============================================================

async function uploadVideo() {

    const fileInput = document.getElementById("videoFile");

    if (!fileInput) {
        alert("Video input not found.");
        return;
    }

    const file = fileInput.files[0];

    if (!file) {
        alert("Please select a video first.");
        return;
    }


    // Make sure it is a video
    if (!file.type.startsWith("video/")) {
        alert("Please select a valid video file.");
        return;
    }


    // Maximum 100 MB
    if (file.size > 100 * 1024 * 1024) {
        alert("Video size must be less than 100 MB.");
        return;
    }


    // Check login
    if (!token) {
        alert("Please login first.");
        return;
    }


    const formData = new FormData();

    formData.append("file", file);


    try {

        console.log("Uploading video...");
        console.log("File:", file.name);
        console.log("Size:", file.size);
        console.log("Type:", file.type);


        const data = await api(
            "/api/instructor/upload/video",
            {
                method: "POST",

                headers: {
                    "Authorization": "Bearer " + token
                },

                body: formData
            }
        );


        // ====================================================
        // IMPORTANT
        // Save Cloudinary URL
        // ====================================================

        uploadedVideoUrl = data.url;


        console.log(
            "Cloudinary Video URL:",
            uploadedVideoUrl
        );


        if (!uploadedVideoUrl) {

            alert(
                "Video uploaded, but Cloudinary URL was not returned."
            );

            return;
        }


        alert("Video uploaded successfully!");


        // Show URL if element exists
        const videoUrlElement =
            document.getElementById("uploadedVideoUrl");

        if (videoUrlElement) {

            videoUrlElement.value = uploadedVideoUrl;

            videoUrlElement.style.display = "block";
        }


        // Show uploaded video preview if video element exists
        const preview =
            document.getElementById("videoPreview");

        if (preview) {

            preview.src = uploadedVideoUrl;

            preview.style.display = "block";

            preview.load();
        }


        // Enable Create Lecture button
        const createLectureButton =
            document.getElementById("createLectureBtn");

        if (createLectureButton) {

            createLectureButton.disabled = false;
        }

    } catch (error) {

        console.error(
            "Video upload failed:",
            error
        );

        alert(
            "Video upload failed: " +
            error.message
        );
    }
}


// ============================================================
// CREATE LECTURE
// ============================================================

async function createLecture() {

    // --------------------------------------------------------
    // Check Cloudinary URL
    // --------------------------------------------------------

    if (!uploadedVideoUrl) {

        alert(
            "Please upload a video first."
        );

        return;
    }


    // --------------------------------------------------------
    // Get form values
    // --------------------------------------------------------

    const sectionId =
        document.getElementById("lectureSectionId")?.value;

    const title =
        document.getElementById("lectureTitle")?.value.trim();

    const description =
        document.getElementById("lectureDescription")?.value.trim();

    const duration =
        document.getElementById("lectureDuration")?.value;

    const lectureOrder =
        document.getElementById("lectureOrder")?.value;

    const freePreview =
        document.getElementById("freePreview")?.checked || false;


    // --------------------------------------------------------
    // Validation
    // --------------------------------------------------------

    if (!sectionId) {

        alert(
            "Please select a section."
        );

        return;
    }


    if (!title) {

        alert(
            "Please enter lecture title."
        );

        return;
    }


    if (!duration) {

        alert(
            "Please enter lecture duration."
        );

        return;
    }


    if (!lectureOrder) {

        alert(
            "Please enter lecture order."
        );

        return;
    }


    // --------------------------------------------------------
    // Create Lecture
    // --------------------------------------------------------

    try {

        console.log(
            "Creating lecture..."
        );

        console.log(
            "Video URL:",
            uploadedVideoUrl
        );


        const data = await api(
            "/api/instructor/lectures",
            {
                method: "POST",

                headers: authHeaders(true),

                body: JSON.stringify({

                    sectionId: Number(sectionId),

                    title: title,

                    description: description,

                    // ========================================
                    // THIS IS THE IMPORTANT PART
                    // ========================================

                    videoUrl: uploadedVideoUrl,

                    durationInMinutes:
                        Number(duration),

                    lectureOrder:
                        Number(lectureOrder),

                    freePreview:
                        freePreview
                })
            }
        );


        console.log(
            "Lecture created:",
            data
        );


        alert(
            "Lecture created successfully!"
        );


        // ----------------------------------------------------
        // Reset form
        // ----------------------------------------------------

        resetLectureForm();


        // ----------------------------------------------------
        // Reload lectures if function exists
        // ----------------------------------------------------

        if (typeof loadLectures === "function") {

            loadLectures(sectionId);
        }


    } catch (error) {

        console.error(
            "Create lecture failed:",
            error
        );

        alert(
            "Could not create lecture: " +
            error.message
        );
    }
}


// ============================================================
// RESET LECTURE FORM
// ============================================================

function resetLectureForm() {

    uploadedVideoUrl = "";


    const title =
        document.getElementById("lectureTitle");

    if (title) {
        title.value = "";
    }


    const description =
        document.getElementById("lectureDescription");

    if (description) {
        description.value = "";
    }


    const duration =
        document.getElementById("lectureDuration");

    if (duration) {
        duration.value = "";
    }


    const order =
        document.getElementById("lectureOrder");

    if (order) {
        order.value = "";
    }


    const file =
        document.getElementById("videoFile");

    if (file) {
        file.value = "";
    }


    const url =
        document.getElementById("uploadedVideoUrl");

    if (url) {

        url.value = "";

        url.style.display = "none";
    }


    const preview =
        document.getElementById("videoPreview");

    if (preview) {

        preview.pause();

        preview.removeAttribute("src");

        preview.load();

        preview.style.display = "none";
    }


    const button =
        document.getElementById("createLectureBtn");

    if (button) {

        button.disabled = true;
    }
}


// ============================================================
// BUTTON EVENT LISTENERS
// ============================================================

document.addEventListener(
    "DOMContentLoaded",
    function () {


        // ----------------------------------------------------
        // Upload Video Button
        // ----------------------------------------------------

        const uploadButton =
            document.getElementById("uploadVideoBtn");

        if (uploadButton) {

            uploadButton.addEventListener(
                "click",
                uploadVideo
            );
        }


        // ----------------------------------------------------
        // Create Lecture Button
        // ----------------------------------------------------

        const createButton =
            document.getElementById("createLectureBtn");

        if (createButton) {

            // Initially disabled
            createButton.disabled = true;


            createButton.addEventListener(
                "click",
                createLecture
            );
        }

    }
);