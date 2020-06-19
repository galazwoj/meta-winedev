require conf/multilib.conf
MULTILIBS = "multilib:lib32"
DEFAULTTUNE_virtclass-multilib-lib32 = "armv7athf-neon-vfpv4"

# Set RPM_PREFER_ELF_ARCH to configure preferred ABI when using rpm packaging
# backend to generate a rootfs, choices are:
# 1: ELF32 wins
# 2: ELF64 wins
RPM_PREFER_ELF_ARCH ?= "2"
