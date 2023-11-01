package de.liruhg.lirucloud.library.directory

class Directories {

    companion object {
        private const val ROOT_PREFIX = "app"
        const val MASTER_ROOT = "$ROOT_PREFIX/master"

        const val MASTER_CONFIGURATION = "$MASTER_ROOT/configuration"
        const val MASTER_KEYS = "$MASTER_ROOT/keys"
        const val MASTER_SOFTWARE = "$MASTER_ROOT/software"
        const val MASTER_SOFTWARE_PROXY = "$MASTER_SOFTWARE/proxy"
        const val MASTER_SOFTWARE_SERVER = "$MASTER_SOFTWARE/server"
        const val MASTER_SOFTWARE_PROXY_PLUGINS = "$MASTER_SOFTWARE_PROXY/plugins"
        const val MASTER_SOFTWARE_SERVER_PLUGINS = "$MASTER_SOFTWARE_SERVER/plugins"
        const val MASTER_GROUPS = "$MASTER_ROOT/groups"
        const val MASTER_GROUPS_PROXY = "$MASTER_GROUPS/proxy"
        const val MASTER_GROUPS_SERVER = "$MASTER_GROUPS/server"
        const val MASTER_TEMPLATE = "$MASTER_ROOT/template"
        const val MASTER_TEMPLATE_PROXY = "$MASTER_TEMPLATE/proxy"
        const val MASTER_TEMPLATE_PROXY_TEMP = "$MASTER_TEMPLATE_PROXY/temp"
        const val MASTER_TEMPLATE_SERVER = "$MASTER_TEMPLATE/server"
        const val MASTER_TEMPLATE_SERVER_TEMP = "$MASTER_TEMPLATE_SERVER/temp"

        const val CLIENT_ROOT = "$ROOT_PREFIX/client"
        const val CLIENT_CONFIGURATION = "$CLIENT_ROOT/configuration"
        const val CLIENT_KEYS = "$CLIENT_ROOT/keys"
        const val CLIENT_STATIC = "$CLIENT_ROOT/static"
        const val CLIENT_STATIC_PROXY = "$CLIENT_STATIC/proxy"
        const val CLIENT_STATIC_SERVER = "$CLIENT_STATIC/server"
        const val CLIENT_CACHED_TEMPLATES = "$CLIENT_ROOT/cached-templates"
        const val CLIENT_CACHED_TEMPLATES_PROXY = "$CLIENT_CACHED_TEMPLATES/proxy"
        const val CLIENT_CACHED_TEMPLATES_SERVER = "$CLIENT_CACHED_TEMPLATES/server"
        const val CLIENT_RUNNING_PROCESSES = "$CLIENT_ROOT/running-processes"
        const val CLIENT_RUNNING_PROCESSES_PROXY = "$CLIENT_RUNNING_PROCESSES/proxy"
        const val CLIENT_RUNNING_PROCESSES_SERVER = "$CLIENT_RUNNING_PROCESSES/server"

        const val PROXY_PLUGINS = "plugins/"
        const val PROXY_PLUGINS_API = "plugins/LiruCloudProxyApi/"
    }
}