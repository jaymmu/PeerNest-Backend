// ============================================================
// PEERNEST APPLICATION
// ============================================================

const API = "http://localhost:8081";


// ============================================================
// AUTH DATA
// ============================================================

let token =
    localStorage.getItem("peernest_token") || "";

let role =
    localStorage.getItem("peernest_role") || "";

let email =
    localStorage.getItem("peernest_email") || "";

let name =
    localStorage.getItem("peernest_name") || "";


// ============================================================
// COURSE DATA
// ============================================================

let allCourses = [];

let selectedCourse = null;

let selectedSections = [];


// ============================================================
// STUDENT PLAYER DATA
// ============================================================

let studentLectures = [];

let currentLectureIndex = -1;

let currentSectionId = null;


// ============================================================
// INSTRUCTOR DATA
// ============================================================

let uploadedVideoUrl = "";

let instructorCourses = [];

let instructorSections = [];


// ============================================================
// DOM HELPER
// ============================================================

function $(id) {

    return document.getElementById(id);

}


// ============================================================
// AUTH HEADERS
// ============================================================

function authHeaders(json = true) {

    const headers = {};


    if (json) {

        headers["Content-Type"] =
            "application/json";

    }


    if (token) {

        headers["Authorization"] =
            "Bearer " + token;

    }


    return headers;
}


// ============================================================
// API FUNCTION
// ============================================================

async function api(path, options = {}) {

    const response =
        await fetch(API + path, options);


    const text =
        await response.text();


    let data;


    try {

        data =
            text
                ? JSON.parse(text)
                : null;

    } catch (error) {

        data = text;

    }


    if (!response.ok) {

        console.error(
            "API ERROR:",
            response.status,
            data
        );


        throw new Error(
            typeof data === "string"
                ? data
                : data?.message ||
                  "Request failed"
        );

    }


    return data;
}


// ============================================================
// MESSAGE
// ============================================================

function showMessage(message) {

    const box = $("messageBox");

    if (!box) return;


    box.textContent = message;

    box.style.display = "block";


    setTimeout(
        () => {

            box.style.display = "none";

        },
        3000
    );
}


// ============================================================
// INITIALIZATION
// ============================================================

document.addEventListener(
    "DOMContentLoaded",
    () => {

        setupEvents();

        updateAuthenticationUI();

    }
);


// ============================================================
// EVENT LISTENERS
// ============================================================

function setupEvents() {


    // --------------------------------------------------------
    // Login
    // --------------------------------------------------------

    $("loginForm")
        ?.addEventListener(
            "submit",
            login
        );


    // --------------------------------------------------------
    // Register
    // --------------------------------------------------------

    $("registerForm")
        ?.addEventListener(
            "submit",
            register
        );


    // --------------------------------------------------------
    // Logout
    // --------------------------------------------------------

    $("logoutBtn")
        ?.addEventListener(
            "click",
            logout
        );


    // --------------------------------------------------------
    // Previous Lecture
    // --------------------------------------------------------

    $("previousLectureBtn")
        ?.addEventListener(
            "click",
            previousStudentLecture
        );


    // --------------------------------------------------------
    // Next Lecture
    // --------------------------------------------------------

    $("nextLectureBtn")
        ?.addEventListener(
            "click",
            nextStudentLecture
        );


    // --------------------------------------------------------
    // Close Player
    // --------------------------------------------------------

    $("closePlayerBtn")
        ?.addEventListener(
            "click",
            closeStudentPlayer
        );


    // --------------------------------------------------------
    // Upload Video
    // --------------------------------------------------------

    $("uploadVideoBtn")
        ?.addEventListener(
            "click",
            uploadVideo
        );


    // --------------------------------------------------------
    // Create Section
    // --------------------------------------------------------

    $("createSectionForm")
        ?.addEventListener(
            "submit",
            createSection
        );


    // --------------------------------------------------------
    // Create Lecture
    // --------------------------------------------------------

    $("createLectureForm")
        ?.addEventListener(
            "submit",
            createLecture
        );


    // --------------------------------------------------------
    // Section change
    // --------------------------------------------------------

    $("lectureSectionId")
        ?.addEventListener(
            "change",
            function () {

                uploadedVideoUrl = "";

                updateCreateLectureButton();

            }
        );

}


// ============================================================
// AUTHENTICATION UI
// ============================================================

function updateAuthenticationUI() {

    if (token) {

        $("authSection").style.display =
            "none";

        $("appSection").style.display =
            "block";

        $("logoutBtn").style.display =
            "inline-block";


        $("currentUser").textContent =
            `${name || email} (${role})`;


        loadCourses();


        if (role === "INSTRUCTOR") {

            $("instructorSection").style.display =
                "block";

            loadInstructorCourses();

        } else {

            $("instructorSection").style.display =
                "none";

        }

    } else {

        $("authSection").style.display =
            "block";

        $("appSection").style.display =
            "none";

        $("logoutBtn").style.display =
            "none";

        $("currentUser").textContent =
            "Not logged in";

    }

}


// ============================================================
// LOGIN
// ============================================================

async function login(event) {

    event.preventDefault();


    const loginEmail =
        $("loginEmail")
            .value
            .trim();


    const password =
        $("loginPassword")
            .value;


    try {

        const data =
            await api(
                "/api/auth/login",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        email: loginEmail,

                        password: password

                    })

                }
            );


        token =
            data.token ||
            data.accessToken ||
            data.jwt ||
            data.jwtToken;


        role =
            data.role || "";


        email =
            data.email ||
            loginEmail;


        name =
            data.name || "";


        if (!token) {

            throw new Error(
                "No JWT token received from server."
            );

        }


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


        showMessage(
            "Login successful!"
        );


        $("loginForm").reset();


        updateAuthenticationUI();


    } catch (error) {

        console.error(
            "Login error:",
            error
        );


        alert(
            "Login failed: " +
            error.message
        );

    }

}


// ============================================================
// REGISTER
// ============================================================

async function register(event) {

    event.preventDefault();


    const registerName =
        $("registerName")
            .value
            .trim();


    const registerEmail =
        $("registerEmail")
            .value
            .trim();


    const registerPassword =
        $("registerPassword")
            .value;


    const registerRole =
        $("registerRole")
            .value;


    try {

        const result =
            await api(
                "/api/auth/register",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        name: registerName,

                        email: registerEmail,

                        password: registerPassword,

                        role: registerRole

                    })

                }
            );


        alert(
            typeof result === "string"
                ? result
                : "Registration successful!"
        );


        $("registerForm").reset();


    } catch (error) {

        console.error(
            "Registration error:",
            error
        );


        alert(
            "Registration failed: " +
            error.message
        );

    }

}


// ============================================================
// LOGOUT
// ============================================================

function logout() {

    token = "";

    role = "";

    email = "";

    name = "";


    localStorage.removeItem(
        "peernest_token"
    );


    localStorage.removeItem(
        "peernest_role"
    );


    localStorage.removeItem(
        "peernest_email"
    );


    localStorage.removeItem(
        "peernest_name"
    );


    closeStudentPlayer();


    updateAuthenticationUI();

}


// ============================================================
// LOAD COURSES
// ============================================================

async function loadCourses() {

    try {

        const courses =
            await api(
                "/api/courses",
                {
                    method: "GET",

                    headers:
                        authHeaders(false)
                }
            );


        allCourses =
            Array.isArray(courses)
                ? courses
                : [];


        renderCourses();


    } catch (error) {

        console.error(
            "Course loading error:",
            error
        );


        $("courseContainer").innerHTML = `

            <p class="loading-message">

                Failed to load courses.

            </p>

        `;

    }

}


// ============================================================
// RENDER COURSES
// ============================================================

function renderCourses() {

    const container =
        $("courseContainer");


    if (!allCourses.length) {

        container.innerHTML = `

            <p class="loading-message">

                No courses available.

            </p>

        `;

        return;
    }


    container.innerHTML =
        allCourses
            .map(
                (course) => `

                <div class="course-card">

                    <div class="course-thumbnail">

                        ${
                            course.thumbnailUrl

                            ? `
                                <img
                                    src="${course.thumbnailUrl}"
                                    alt="${escapeHtml(course.title)}"
                                >
                              `

                            : `
                                <div class="no-thumbnail">
                                    PeerNest
                                </div>
                              `
                        }

                    </div>


                    <div class="course-card-content">

                        <h3>
                            ${escapeHtml(course.title)}
                        </h3>


                        <p>

                            ${escapeHtml(
                                course.description || ""
                            )}

                        </p>


                        <div class="course-details">

                            <span>
                                ${
                                    course.free
                                        ? "Free"
                                        : "₹" +
                                          (course.price || 0)
                                }
                            </span>


                            <span>
                                ${
                                    course.level ||
                                    ""
                                }
                            </span>

                        </div>


                        <button
                            type="button"
                            class="primary-btn"
                            onclick="openCourse(${course.id})"
                        >
                            View Course
                        </button>

                    </div>

                </div>

            `
            )
            .join("");

}


// ============================================================
// OPEN COURSE
// ============================================================

async function openCourse(courseId) {

    selectedCourse =
        allCourses.find(
            course =>
                Number(course.id) ===
                Number(courseId)
        );


    if (!selectedCourse) {

        alert(
            "Course not found."
        );

        return;
    }


    $("studentPlayerSection")
        .style.display =
        "block";


    $("studentCourseTitle")
        .textContent =
        selectedCourse.title || "Course";


    $("studentCourseDescription")
        .textContent =
        selectedCourse.description || "";


    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });


    await loadCourseSections(
        courseId
    );

}


// ============================================================
// LOAD COURSE SECTIONS
// ============================================================

async function loadCourseSections(
    courseId
) {

    const container =
        $("studentSectionsContainer");


    container.innerHTML = `

        <p class="loading-message">
            Loading sections...
        </p>

    `;


    try {

        const sections =
            await api(
                `/api/instructor/sections/course/${courseId}`,
                {
                    method: "GET",

                    headers:
                        authHeaders(false)
                }
            );


        selectedSections =
            Array.isArray(sections)
                ? sections
                : [];


        renderStudentSections();


    } catch (error) {

        console.error(
            "Section loading error:",
            error
        );


        container.innerHTML = `

            <p class="loading-message">

                Unable to load course sections.

            </p>

        `;

    }

}


// ============================================================
// RENDER STUDENT SECTIONS
// ============================================================

function renderStudentSections() {

    const container =
        $("studentSectionsContainer");


    if (!selectedSections.length) {

        container.innerHTML = `

            <p class="loading-message">

                No sections available.

            </p>

        `;

        return;
    }


    container.innerHTML =
        selectedSections
            .map(
                (section, index) => `

                <div class="student-section">

                    <div
                        class="student-section-header"
                        onclick="loadStudentLectures(${section.id})"
                    >

                        <h3
                            class="student-section-title"
                        >
                            ${index + 1}.
                            ${escapeHtml(
                                section.title
                            )}
                        </h3>


                        <span>
                            ▶
                        </span>

                    </div>


                    <div
                        id="sectionLectures-${section.id}"
                        class="student-lecture-list"
                    ></div>

                </div>

            `
            )
            .join("");

}


// ============================================================
// LOAD STUDENT LECTURES
// ============================================================

async function loadStudentLectures(
    sectionId
) {

    currentSectionId =
        sectionId;


    const lectureContainer =
        $(
            `sectionLectures-${sectionId}`
        );


    if (lectureContainer) {

        lectureContainer.innerHTML = `

            <p class="loading-message">
                Loading lectures...
            </p>

        `;

    }


    try {

        const lectures =
            await api(
                `/api/student/lectures/section/${sectionId}`,
                {
                    method: "GET",

                    headers:
                        authHeaders(false)
                }
            );


        studentLectures =
            Array.isArray(lectures)
                ? lectures
                : [];


        renderStudentLectures(
            sectionId
        );


    } catch (error) {

        console.error(
            "Lecture loading error:",
            error
        );


        if (lectureContainer) {

            lectureContainer.innerHTML = `

                <p class="loading-message">
                    Failed to load lectures.
                </p>

            `;

        }

    }

}


// ============================================================
// RENDER STUDENT LECTURES
// ============================================================

function renderStudentLectures(
    sectionId
) {

    const container =
        $(
            `sectionLectures-${sectionId}`
        );


    if (!container) {

        return;
    }


    if (!studentLectures.length) {

        container.innerHTML = `

            <p class="loading-message">
                No lectures available.
            </p>

        `;

        return;
    }


    container.innerHTML =
        studentLectures
            .map(
                (lecture, index) => `

                <button
                    type="button"
                    class="student-lecture-item"
                    data-index="${index}"
                    onclick="selectStudentLecture(${index})"
                >

                    <span
                        class="student-lecture-name"
                    >

                        ${index + 1}.
                        ${escapeHtml(
                            lecture.title
                        )}

                    </span>


                    <span
                        class="student-lecture-duration"
                    >

                        ${
                            lecture.durationInMinutes ||
                            0
                        }
                        min

                    </span>

                </button>

            `
            )
            .join("");

}


// ============================================================
// SELECT LECTURE
// ============================================================

function selectStudentLecture(
    index
) {

    if (
        index < 0 ||
        index >= studentLectures.length
    ) {

        return;
    }


    const lecture =
        studentLectures[index];


    currentLectureIndex =
        index;


    const player =
        $("studentVideoPlayer");


    // --------------------------------------------------------
    // VIDEO
    // --------------------------------------------------------

    if (player) {

        if (lecture.videoUrl) {

            player.src =
                lecture.videoUrl;

            player.load();

        } else {

            player.removeAttribute(
                "src"
            );

            player.load();

            alert(
                "This lecture does not have a video."
            );

        }

    }


    // --------------------------------------------------------
    // TITLE
    // --------------------------------------------------------

    $("studentLectureTitle")
        .textContent =
        lecture.title ||
        "Untitled Lecture";


    // --------------------------------------------------------
    // DESCRIPTION
    // --------------------------------------------------------

    $("studentLectureDescription")
        .textContent =
        lecture.description ||
        "";


    // --------------------------------------------------------
    // DURATION
    // --------------------------------------------------------

    $("studentLectureDuration")
        .textContent =
        `Duration: ${
            lecture.durationInMinutes || 0
        } min`;


    // --------------------------------------------------------
    // PREVIEW
    // --------------------------------------------------------

    $("studentLecturePreview")
        .textContent =
        lecture.freePreview
            ? "Free Preview"
            : "Course Lecture";


    // --------------------------------------------------------
    // HIGHLIGHT
    // --------------------------------------------------------

    document
        .querySelectorAll(
            ".student-lecture-item"
        )
        .forEach(
            button => {

                button.classList.remove(
                    "active"
                );

            }
        );


    const activeButton =
        document.querySelector(
            `.student-lecture-item[data-index="${index}"]`
        );


    if (activeButton) {

        activeButton.classList.add(
            "active"
        );

    }


    // --------------------------------------------------------
    // NAVIGATION
    // --------------------------------------------------------

    updateLectureNavigation();


    // --------------------------------------------------------
    // PROGRESS
    // --------------------------------------------------------

    $("studentCourseProgress")
        .textContent =
        `Lecture ${
            index + 1
        } of ${
            studentLectures.length
        }`;


    // --------------------------------------------------------
    // Scroll video into view
    // --------------------------------------------------------

    $("studentVideoPlayer")
        ?.scrollIntoView({
            behavior: "smooth",
            block: "center"
        });

}


// ============================================================
// UPDATE LECTURE NAVIGATION
// ============================================================

function updateLectureNavigation() {

    const previous =
        $("previousLectureBtn");


    const next =
        $("nextLectureBtn");


    if (previous) {

        previous.disabled =
            currentLectureIndex <= 0;

    }


    if (next) {

        next.disabled =
            currentLectureIndex < 0 ||
            currentLectureIndex >=
            studentLectures.length - 1;

    }

}


// ============================================================
// PREVIOUS LECTURE
// ============================================================

function previousStudentLecture() {

    if (
        currentLectureIndex > 0
    ) {

        selectStudentLecture(
            currentLectureIndex - 1
        );

    }

}


// ============================================================
// NEXT LECTURE
// ============================================================

function nextStudentLecture() {

    if (
        currentLectureIndex >= 0 &&
        currentLectureIndex <
        studentLectures.length - 1
    ) {

        selectStudentLecture(
            currentLectureIndex + 1
        );

    }

}


// ============================================================
// CLOSE PLAYER
// ============================================================

function closeStudentPlayer() {

    const player =
        $("studentVideoPlayer");


    if (player) {

        player.pause();

        player.removeAttribute(
            "src"
        );

        player.load();

    }


    $("studentPlayerSection")
        .style.display =
        "none";


    studentLectures = [];

    currentLectureIndex = -1;

}


// ============================================================
// INSTRUCTOR COURSES
// ============================================================

async function loadInstructorCourses() {

    try {

        const courses =
            await api(
                "/api/courses",
                {
                    method: "GET",

                    headers:
                        authHeaders(false)
                }
            );


        instructorCourses =
            Array.isArray(courses)
                ? courses
                : [];


        populateCourseSelect();


    } catch (error) {

        console.error(
            "Instructor courses error:",
            error
        );

    }

}


// ============================================================
// COURSE SELECT
// ============================================================

function populateCourseSelect() {

    const select =
        $("sectionCourseId");


    if (!select) return;


    select.innerHTML = `

        <option value="">
            Select Course
        </option>

    `;


    instructorCourses
        .forEach(
            course => {

                select.innerHTML += `

                    <option
                        value="${course.id}"
                    >

                        ${escapeHtml(
                            course.title
                        )}

                    </option>

                `;

            }
        );

}


// ============================================================
// CREATE SECTION
// ============================================================

async function createSection(
    event
) {

    event.preventDefault();


    const courseId =
        $("sectionCourseId")
            .value;


    const title =
        $("sectionTitle")
            .value
            .trim();


    const description =
        $("sectionDescription")
            .value
            .trim();


    const sectionOrder =
        $("sectionOrder")
            .value;


    if (!courseId) {

        alert(
            "Please select a course."
        );

        return;
    }


    try {

        const data =
            await api(
                "/api/instructor/sections",
                {
                    method: "POST",

                    headers:
                        authHeaders(true),

                    body:
                        JSON.stringify({

                            courseId:
                                Number(
                                    courseId
                                ),

                            title:
                                title,

                            description:
                                description,

                            sectionOrder:
                                Number(
                                    sectionOrder
                                )

                        })

                }
            );


        console.log(
            "Section created:",
            data
        );


        alert(
            "Section created successfully!"
        );


        $("createSectionForm")
            .reset();


        await loadInstructorSections(
            courseId
        );


    } catch (error) {

        console.error(
            "Create section error:",
            error
        );


        alert(
            "Could not create section: " +
            error.message
        );

    }

}


// ============================================================
// LOAD INSTRUCTOR SECTIONS
// ============================================================

async function loadInstructorSections(
    courseId
) {

    try {

        const sections =
            await api(
                `/api/instructor/sections/course/${courseId}`,
                {
                    method: "GET",

                    headers:
                        authHeaders(false)
                }
            );


        instructorSections =
            Array.isArray(sections)
                ? sections
                : [];


        populateSectionSelect();


    } catch (error) {

        console.error(
            "Sections error:",
            error
        );

    }

}


// ============================================================
// COURSE SELECT CHANGE
// ============================================================

document.addEventListener(
    "DOMContentLoaded",
    () => {

        $("sectionCourseId")
            ?.addEventListener(
                "change",
                async function () {

                    if (this.value) {

                        await loadInstructorSections(
                            this.value
                        );

                    } else {

                        $("lectureSectionId")
                            .innerHTML = `

                                <option value="">
                                    Select Section
                                </option>

                            `;

                    }

                }
            );

    }
);


// ============================================================
// POPULATE SECTION SELECT
// ============================================================

function populateSectionSelect() {

    const select =
        $("lectureSectionId");


    if (!select) return;


    select.innerHTML = `

        <option value="">
            Select Section
        </option>

    `;


    instructorSections
        .forEach(
            section => {

                select.innerHTML += `

                    <option
                        value="${section.id}"
                    >

                        ${escapeHtml(
                            section.title
                        )}

                    </option>

                `;

            }
        );

}


// ============================================================
// UPLOAD VIDEO
// ============================================================

async function uploadVideo() {

    const fileInput =
        $("videoFile");


    const file =
        fileInput?.files[0];


    if (!file) {

        alert(
            "Please select a video first."
        );

        return;
    }


    if (
        !file.type.startsWith(
            "video/"
        )
    ) {

        alert(
            "Please select a valid video file."
        );

        return;
    }


    if (
        file.size >
        100 * 1024 * 1024
    ) {

        alert(
            "Video must be smaller than 100 MB."
        );

        return;
    }


    if (!token) {

        alert(
            "Please login first."
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

        $("uploadVideoBtn")
            .disabled = true;


        $("uploadVideoBtn")
            .textContent =
            "Uploading...";


        const data =
            await api(
                "/api/instructor/upload/video",
                {
                    method: "POST",

                    headers: {
                        "Authorization":
                            "Bearer " +
                            token
                    },

                    body: formData
                }
            );


        uploadedVideoUrl =
            data.url;


        console.log(
            "Cloudinary URL:",
            uploadedVideoUrl
        );


        if (!uploadedVideoUrl) {

            throw new Error(
                "Cloudinary URL was not returned."
            );

        }


        $("uploadedVideoUrl")
            .value =
            uploadedVideoUrl;


        $("uploadedVideoUrl")
            .style.display =
            "block";


        // ----------------------------------------------------
        // Preview
        // ----------------------------------------------------

        const preview =
            $("videoPreview");


        preview.src =
            uploadedVideoUrl;


        preview.style.display =
            "block";


        preview.load();


        updateCreateLectureButton();


        alert(
            "Video uploaded successfully!"
        );


    } catch (error) {

        console.error(
            "Video upload error:",
            error
        );


        alert(
            "Video upload failed: " +
            error.message
        );


    } finally {

        $("uploadVideoBtn")
            .disabled = false;


        $("uploadVideoBtn")
            .textContent =
            "Upload Video";

    }

}


// ============================================================
// ENABLE/DISABLE CREATE LECTURE
// ============================================================

function updateCreateLectureButton() {

    const button =
        $("createLectureBtn");


    if (!button) return;


    button.disabled =
        !uploadedVideoUrl;

}


// ============================================================
// CREATE LECTURE
// ============================================================

async function createLecture(
    event
) {

    event.preventDefault();


    if (!uploadedVideoUrl) {

        alert(
            "Please upload the lecture video first."
        );

        return;
    }


    const sectionId =
        $("lectureSectionId")
            .value;


    const title =
        $("lectureTitle")
            .value
            .trim();


    const description =
        $("lectureDescription")
            .value
            .trim();


    const duration =
        $("lectureDuration")
            .value;


    const lectureOrder =
        $("lectureOrder")
            .value;


    const freePreview =
        $("freePreview")
            .checked;


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


    try {

        const data =
            await api(
                "/api/instructor/lectures",
                {
                    method: "POST",

                    headers:
                        authHeaders(true),

                    body:
                        JSON.stringify({

                            sectionId:
                                Number(
                                    sectionId
                                ),

                            title:
                                title,

                            description:
                                description,

                            videoUrl:
                                uploadedVideoUrl,

                            durationInMinutes:
                                Number(
                                    duration
                                ),

                            lectureOrder:
                                Number(
                                    lectureOrder
                                ),

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


        resetLectureForm();


    } catch (error) {

        console.error(
            "Create lecture error:",
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

    $("createLectureForm")
        ?.reset();


    uploadedVideoUrl = "";


    $("uploadedVideoUrl")
        .value = "";


    $("uploadedVideoUrl")
        .style.display =
        "none";


    const preview =
        $("videoPreview");


    preview.pause();

    preview.removeAttribute(
        "src"
    );

    preview.load();

    preview.style.display =
        "none";


    updateCreateLectureButton();

}


// ============================================================
// HTML ESCAPE
// ============================================================

function escapeHtml(value) {

    if (
        value === null ||
        value === undefined
    ) {

        return "";

    }


    return String(value)

        .replaceAll(
            "&",
            "&amp;"
        )

        .replaceAll(
            "<",
            "&lt;"
        )

        .replaceAll(
            ">",
            "&gt;"
        )

        .replaceAll(
            '"',
            "&quot;"
        )

        .replaceAll(
            "'",
            "&#039;"
        );

}