# Qualcomm proprietary multimedia, graphics and accelerator extensions for
# Arduino images. Inspired by qcom-multimedia-proprietary-image from meta-qcom-distro.

CORE_IMAGE_EXTRA_INSTALL:append = " \
    packagegroup-qcom-utilities-gpu-utils \
    thermald \
    userspace-resource-manager \
    userspace-resource-manager-extensions \
"

CORE_IMAGE_EXTRA_INSTALL:append = " \
    camera-service \
    camx-dlkm \
    camx-hamoa \
    camx-kodiak \
    camx-lemans \
    camx-nhx \
    camx-talos \
    gst-plugins-imsdk-prop \
    iris-video-dlkm \
    kgsl-dlkm \
    libdiag-bin \
    onnxruntime-qnn \
    qairt-sdk \
    qcom-adreno \
    qcom-sensors-binaries \
"

CORE_IMAGE_EXTRA_INSTALL:append:aarch64 = " \
    gst-plugins-imsdk-oss \
    gst-plugins-imsdk-python \
"

CORE_IMAGE_EXTRA_INSTALL:append:monza = " libqcnpuperf libqcnpuperf-cli libqcnpuperf-smi"

CORE_IMAGE_EXTRA_INSTALL:append = " fastrpc-tests"

# Access to the FastRPC device nodes for the arduino user
EXTRA_USERS_PARAMS += " \
    groupadd -r -f fastrpc; \
    usermod -aG fastrpc arduino; \
"

REQUIRED_DISTRO_FEATURES += "wayland"
