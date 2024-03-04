PACKAGECONFIG_class-native = "fdt sdl kvm gtk+ virglrenderer glx virtfs"
PACKAGECONFIG_class-nativesdk = "fdt sdl kvm gtk+ virglrenderer glx virtfs"

EXTRA_OECONF += "       \
	--enable-sdl    \
	--enable-opengl \
	--enable-virglrenderer \
	"
