FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://gdbm-fix-link-failure-against-gcc-10.patch \
    "
