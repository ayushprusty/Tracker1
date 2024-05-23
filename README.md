Object Tracker Android App

***HACKY SOLUTION***
For single-point body tracking, what we can do is we can provide our users with a color patch and then track that color while they are playing the game. This can be used as a point of reference for further analysis.

This Android application uses the OpenCV library to detect and track yellow objects in real time using the device's camera. The app demonstrates the use of fundamental computer vision techniques, including color filtering, contour detection, and object tracking.
Code Explanation
The MainActivity class is the application's entry point and handles the initialization of the OpenCV library, camera permissions, and the camera preview view.

1. OpenCV Initialization: The app uses the OpenCVLoader class to load the OpenCV library. If the library is not found within the package, it attempts to load it from the OpenCV Manager.
Camera Permissions: The app requests camera permissions from the user at runtime, which is required for accessing the device's camera on Android 6.0 (Marshmallow) and later versions.
Camera Preview: The app uses the CameraBridgeViewBase class from the OpenCV library to display the camera preview. The MainActivity class implements the CvCameraViewListener2 interface to handle the camera frame processing.

2. Color Filtering: The app uses color filtering to detect yellow objects in the camera frame. It converts the frame from the RGB color space to the HSV color space, which is more suitable for color-based segmentation. Then, it creates a mask by applying thresholding on the HSV values within a predefined range for the yellow color (LOWER_YELLOW and UPPER_YELLOW constants).
Contour Detection: After creating the mask, the app uses the findContours function from OpenCV to detect contours (outlines of objects) in the mask. It retrieves the external contours using the RETR_EXTERNAL retrieval mode and approximates them using the CHAIN_APPROX_SIMPLE approximation method.

3. Object Tracking: The app iterates through the detected contours and finds the most prominent contour by area. It then calculates the bounding rectangle for the most significant contour using the boundingRect function from OpenCV. Finally, using the rectangle function, it draws a green rectangle around the detected object in the camera frame.
Frame Rendering: The processed frame with the detected object and the bounding rectangle is returned from the onCameraFrame method and displayed in the camera preview view.

Computer Vision Principles
This app demonstrates the following fundamental computer vision principles:

1.Color Spaces: The app converts the camera frame from the RGB color space to the HSV color space, which is more suitable for color-based segmentation and processing.

2.Color Filtering: The app uses color filtering to create a binary mask that separates the object of interest (yellow objects) from the background.

3.Contour Detection: The app uses OpenCV's findContours function to detect objects' outlines in the binary mask.

4.Contour Analysis: The app analyzes the detected contours to identify the most significant contour likely to be the object of interest.

5.Bounding Box Calculation: The app calculates the bounding rectangle for the most prominent contour, which provides a rough localization of the detected object.

6.Object Tracking: The app tracks the detected object in real time by continuously processing the camera frames and updating the bounding rectangle around the most prominent contour.

These principles form the foundation for many computer vision applications, such as object detection, object tracking, and image segmentation.
