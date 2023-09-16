package metospherus.app.utilities

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import metospherus.app.R

@GlideModule
class MyAppGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        // Set the default format to ARGB_8888 for better image quality
        builder.setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))

        // Enable hardware bitmap optimization for better performance
        builder.setDefaultRequestOptions(RequestOptions().disallowHardwareConfig())

        // Apply disk and memory cache strategies
        builder.setMemoryCache(LruResourceCache(10 * 1024 * 1024)) // 10MB memory cache
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, 50 * 1024 * 1024)) // 50MB disk cache

        // Apply other options as needed, such as placeholder and error images
        builder.setDefaultRequestOptions(
            RequestOptions()
                .placeholder(R.drawable.splash_logo) // Placeholder image
                .error(R.drawable.round_error) // Error image
        )
    }
}