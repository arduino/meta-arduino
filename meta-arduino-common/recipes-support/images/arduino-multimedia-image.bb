DESCRIPTION = "Arduino multimedia image with GStreamer, camera and display support. \
Builds on the Weston image to add video capture (V4L2), hardware-accelerated \
GStreamer pipelines and GPU utilities. Platform-specific camera stacks and \
proprietary codecs are injected via bbappend files in the BSP layers."

LICENSE = "MIT"

require recipes-support/images/arduino-weston-image.bb

CORE_IMAGE_EXTRA_INSTALL += " \
    gstreamer1.0 \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
    v4l-utils \
    gst-instruments \
"
