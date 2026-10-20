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
    qwes \
"

CORE_IMAGE_EXTRA_INSTALL:append:aarch64 = " \
    gst-plugins-imsdk-oss \
    gst-plugins-imsdk-python \
"

CORE_IMAGE_EXTRA_INSTALL:append:monza = " libqcnpuperf libqcnpuperf-cli"

CORE_IMAGE_EXTRA_INSTALL:append = " fastrpc-tests"

REQUIRED_DISTRO_FEATURES += "wayland"
