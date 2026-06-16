# Inject QCOM proprietary multimedia packages into arduino-multimedia-image.
# Mirrors the content of qcom-multimedia-proprietary-image.bb from meta-qcom-distro
# (camera stack, GPU/adreno, DSP kernel modules, GStreamer IMSDK plugins, WLAN
# licence enforcement) plus arduino-specific NPU metrics support.

CORE_IMAGE_EXTRA_INSTALL:append = " \
    camera-service \
    camx-dlkm \
    camx-kodiak \
    camx-lemans \
    camx-nhx \
    camx-talos \
    gst-plugins-imsdk-prop \
    iris-video-dlkm \
    kgsl-dlkm \
    libdiag-bin \
    qcom-adreno \
    qcom-sensors-binaries \
    qwes \
    libqcnpuperf \
    ${@bb.utils.contains('BBFILE_COLLECTIONS', 'meta-audioreach', 'packagegroup-audioreach', '', d)} \
"

# QCOM targets always use Wayland; guard against incomplete distro configs.
REQUIRED_DISTRO_FEATURES += "wayland"
