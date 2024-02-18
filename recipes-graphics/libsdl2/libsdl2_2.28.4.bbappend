inherit multilib_header

# Fix multilib issue:
#
#    file /usr/include/SDL2/SDL_config.h conflicts between attempted installs of
# lib32-libsdl2-2.0-dev-2.0.12-r0.armv7at2hf_neon_vfpv4 and libsdl2-2.0-dev-2.0.12-r0.aarch64

do_install:append() {
    oe_multilib_header SDL2/SDL_config.h
}
