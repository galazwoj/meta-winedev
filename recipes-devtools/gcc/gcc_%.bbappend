# Fix multilib issue:
#
#  these are symlinks created by gcc-target.inc
#
#  file /usr/bin/gcc from install of lib32-gcc-symlinks-9.3.0-r0.armv7at2hf_neon_vfpv4 conflicts with file from package gcc-symlinks-9.3.0-r0.aarch64
#  file /usr/bin/cpp from install of lib32-cpp-symlinks-9.3.0-r0.armv7at2hf_neon_vfpv4 conflicts with file from package cpp-symlinks-9.3.0-r0.aarch64
#  file /usr/bin/g++ from install of lib32-g++-symlinks-9.3.0-r0.armv7at2hf_neon_vfpv4 conflicts with file from package g++-symlinks-9.3.0-r0.aarch64
#

inherit multilib_script

# MULTILIB_SCRIPTS = "gcc-symlinks:${bindir}/gcc cpp-symlinks:${bindir}/cpp g++-symlinks:${bindir}/g++"
