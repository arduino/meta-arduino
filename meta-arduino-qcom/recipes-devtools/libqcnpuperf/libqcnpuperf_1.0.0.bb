SUMMARY = "Lightweight NPU metrics collection library"
DESCRIPTION = "libqcnpuperf provides three simple APIs (qcom_dsp_init, \
qcom_dsp_get_prof_data, qcom_dsp_deinit) to collect NPU/Hexagon DSP \
performance metrics via FastRPC. Suitable for profiling tools, Perfetto \
tracing pipelines and CI/CD performance monitoring."
HOMEPAGE = "https://github.com/qualcomm/libqcnpuperf"

LICENSE = "BSD-3-Clause-Clear"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=223037c4be0bfc6cf757035432adf983"

DEPENDS = "fastrpc"

SRC_URI = "git://github.com/qualcomm/libqcnpuperf;protocol=https;nobranch=1;tag=${PV}"
SRCREV = "b2ec245d3b0d98cdb3df75f22ba7209e461c8099"

S = "${UNPACKDIR}/git"

inherit cmake pkgconfig

# Disable the ncurses-based CLI sample by default to keep the recipe lean;
# enable with PACKAGECONFIG += "cli" if you want the qcnpuperf_cli binary.
PACKAGECONFIG ??= ""
PACKAGECONFIG[cli] = "-DQCNPU_PERF_BUILD_CLI=ON,-DQCNPU_PERF_BUILD_CLI=OFF,ncurses"

# Build and runtime require aarch64 with Hexagon DSP / FastRPC support.
COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE:aarch64 = "(.*)"

RDEPENDS:${PN} += "fastrpc"
