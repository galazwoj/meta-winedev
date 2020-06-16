inherit multilib_header

# Fix multilib issue:
#
# file file /usr/include/libpng16/pnglibconf.h conflicts between attempted installs of
#        lib32-libpng16-dev-1.6.35-r0.armv7at2hf_neon_vfpv4 and libpng16-dev-1.6.35-r0.aarch64

do_install_append() {
    oe_multilib_header libpng16/pnglibconf.h
}
