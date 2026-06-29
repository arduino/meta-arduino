FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

do_update_sistemd_service() {
    bbwarn Substitution of adb home folder to fio
    cd ${WORKDIR}
    sed -i 's#WorkingDirectory=/home/arduino#WorkingDirectory=/home/fio#g' \
        android-tools-adbd.service
    # Comment out any line that contains only 'WorkingDirectory'
    sed -i '/^WorkingDirectory/s/^/#/' android-tools-adbd.service
}

addtask do_update_sistemd_service before do_configure after do_patch
