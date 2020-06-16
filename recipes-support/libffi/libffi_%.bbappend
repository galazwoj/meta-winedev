inherit multilib_header

# Fix multilib issue:
#
#  file /usr/include/ffitarget.h conflicts between attempted installs of
#    lib32-libffi-dev-3.3-r0.armv7at2hf_neon_vfpv4 and libffi-dev-3.3-r0.aarch64

do_install_append() {
    oe_multilib_header ffitarget.h
}
