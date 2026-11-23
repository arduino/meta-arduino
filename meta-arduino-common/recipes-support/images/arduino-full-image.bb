DESCRIPTION = "Arduino full system image that includes docker, weston, \
multimedia packages (VPU and GPU), and the Arduino App CLI orchestrator."

LICENSE = "MIT"

require recipes-support/images/arduino-container-image.bb

# Arduino App CLI orchestrator
CORE_IMAGE_EXTRA_INSTALL += " \
    arduino-app-cli \
    arduino-app-assets \
"
