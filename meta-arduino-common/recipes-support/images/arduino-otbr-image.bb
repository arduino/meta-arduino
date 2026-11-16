DESCRIPTION = "Arduino otbr image that includes \
multimedia packages (VPU and GPU) when available."

LICENSE = "MIT"

inherit core-image

IMAGE_FEATURES += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', '', \
       bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11-base', '', d), d)} \
"

IMAGE_FEATURES:remove = "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'x11-base', '', d)}"

require recipes-support/images/arduino-image.inc

CORE_IMAGE_EXTRA_INSTALL += " \
    otbr \
    openthread \
    openthread-iwxxx-spi \
"
