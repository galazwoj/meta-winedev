# Apply various fixes from openembedded-core 'meta/recipes-core/ovmf/ovmf'
# Mostly GCC 8+ related (stringop-truncation detection)
FILESEXTRAPATHS_append := "${THISDIR}/${PN}"

SRC_URI += "\
    file://0003-BaseTools-makefile-adjust-to-build-in-under-bitbake.patch \
    file://VfrCompile-increase-path-length-limit.patch \
    file://0001-BaseTools-header.makefile-add-Wno-stringop-truncatio.patch \
    file://0002-BaseTools-header.makefile-add-Wno-restrict.patch \
    file://0003-BaseTools-header.makefile-revert-gcc-8-Wno-xxx-optio.patch \
    file://0004-BaseTools-GenVtf-silence-false-stringop-overflow-war.patch \
"

# Fix Python 2.x usage for old fork
SRC_URI += "\
    file://0001-uefi-tools-python2.patch \
    file://0001-uefi-l-loader-python2.patch \
"

# FIX incremental build:
# 
# | ln: failed to create symbolic link './bl1.bin': File exists
# | WARNING: ... edk2-hikey960/0.0+AUTOINC+77326b5a15-ed8112606c-r0/temp/run.do_compile.7151:1 exit 1
#    from 'ln -s .../git/Build/HiKey960/RELEASE_GCC49/FV/bl1.bin'
# ...
# ERROR: Task (.../meta-96boards/recipes-bsp/uefi/edk2-hikey960_git.bb:do_compile) failed with exit code '1'
#
# NOTE: we can't "override" another _append: the one in the original recipe will still be appended,
#       hence insert helper task before do_compile.
do_fix_compile() {
    # remove symlinks or do_compile() will fail on existing ones
    if [ -d "${EDK2_DIR}/l-loader" ]; then
        (cd ${EDK2_DIR}/l-loader && rm -f bl1.bin bl2.bin fip.bin BL33_AP_UEFI.fd)
    fi
}
addtask fix_compile after do_configure before do_compile
