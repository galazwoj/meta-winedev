# https://developer.arm.com/products/software/mali-drivers/user-space
MALI_USERLAND_LIBARIES ?= "mali-userland"

# Helper function for overloading the default EGL/GLES implementation.
# The Mali libraries provided by ARM are compatible with the Mesa headers
# and it is safe to use with user space applications linked against Mesa.
def get_egl_handler(d, target):
    """  Overloading the default EGL/GLES implementation."""
    features = d.getVar('MACHINE_FEATURES', True).split()
    tunes = d.getVar("TUNE_FEATURES", True)
    mali_libs = d.getVar('MALI_USERLAND_LIBARIES', True);
    if 'mali-gpu' in features and tunes == "aarch64":
        provider = mali_libs.split()[0]
    else:
        provider = "mesa"
    return provider;

MULTI_PROVIDER_WHITELIST += "virtual/libgl virtual/egl virtual/libgles1 virtual/libgles2"

PREFERRED_PROVIDER_virtual/egl = "${@get_egl_handler(d, 'egl')}"
PREFERRED_PROVIDER_virtual/libgles1 = "${@get_egl_handler(d, 'libgles1')}"
PREFERRED_PROVIDER_virtual/libgles2 = "${@get_egl_handler(d, 'libgles2')}"
