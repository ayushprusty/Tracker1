package org.opencv.android;

/**
 * Interface for callback object in case of OpenCV library installation.
 */
public interface InstallCallbackInterface {
    /**
     * Approve the installation.
     */
    void install();

    /**
     * Cancel the installation.
     */
    void cancel();
}
