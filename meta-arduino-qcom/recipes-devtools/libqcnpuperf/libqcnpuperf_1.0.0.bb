SUMMARY = "Lightweight NPU metrics collection library"
DESCRIPTION = "libqcnpuperf provides three simple APIs (qcom_dsp_init, \
qcom_dsp_get_prof_data, qcom_dsp_deinit) to collect NPU/Hexagon DSP \
performance metrics via FastRPC. Suitable for profiling tools, Perfetto \
tracing pipelines and CI/CD performance monitoring."
HOMEPAGE = "https://github.com/qualcomm/libqcnpuperf"

LICENSE = "BSD-3-Clause-Clear"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=8ea33b4e44af458236377788d0c61379"

DEPENDS = "fastrpc"

SRC_URI = "git://github.com/qualcomm/libqcnpuperf;protocol=https;nobranch=1 \
           file://0001-examples-add-lightweight-qcnpu-smi-tool-with-JSON-ou.patch \
          "
SRCREV = "d452cbcf505a1a87b52ba567244dab6332c2acd2"

inherit cmake pkgconfig

# Enable the lightweight qcnpu-smi tool by default.
PACKAGECONFIG ??= "smi"
PACKAGECONFIG[cli] = "-DQCNPU_PERF_BUILD_CLI=ON,-DQCNPU_PERF_BUILD_CLI=OFF,ncurses"
PACKAGECONFIG[smi] = "-DQCNPU_PERF_BUILD_SMI=ON,-DQCNPU_PERF_BUILD_SMI=OFF"

PACKAGES =+ "${PN}-cli ${PN}-smi"
FILES:${PN}-cli = "${bindir}/qcnpuperf_cli"
FILES:${PN}-smi = "${bindir}/qcnpu-smi"

# Build and runtime require aarch64 with Hexagon DSP / FastRPC support.
COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE:aarch64 = "(.*)"

RDEPENDS:${PN} += "fastrpc"
