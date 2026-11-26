DESCRIPTION = "Arduino full system image that includes docker, weston, \
multimedia packages (VPU and GPU), and the Arduino App CLI orchestrator."

LICENSE = "MIT"

require recipes-support/images/arduino-container-image.bb

# Arduino App CLI orchestrator (arduino-app-cli-daemon pulls in arduino-app-cli)
CORE_IMAGE_EXTRA_INSTALL += " \
    arduino-app-cli-daemon \
    arduino-app-cli-assets \
    arduino-app-cli-examples \
"
