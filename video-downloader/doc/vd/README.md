### Dailymotion

1. WebViewClient.onLoadResource过滤包含"https://www.dailymotion.com/player/metadata/video"的链接
2. 请求该[链接](https://www.dailymotion.com/player/metadata/video/x86dm4b?embedder=https%3A%2F%2Fwww.dailymotion.com%2F&referer=&app=com.dailymotion.neon&client_type=webapp&dmV1st=8D2ADA853F07EAE5E453FCE3AEF81602&dmTs=823288&section_type=player&component_style=_)，获取metadata（dailymotion_meta.json）

从中提取出title、posters和qualities字段。title是视频标题，posters是视频预览图，qualities包含请求视频实际地址的url

3. 请求[qualities/auto/url](https://www.dailymotion.com/cdn/manifest/video/x86dm4b.m3u8?sec=-2E6ElmfWQH818RuBuFLqBYkr-PWo2XB_uMqz1o5fQeucrj2wiUgsJGTImfYM0zEDzzpcXAwjt2fcKGuOuO1iw&dmTs=823288&dmV1st=8D2ADA853F07EAE5E453FCE3AEF81602)，获取manifest（dailymotion_manifest.json）

通过正则表达式"NAME=\"(\\d+)\".*(http.*\\.mp4)"，提取出清晰度和mp4地址。提取出的结果需要滤重，因为每个清晰度都给了两组链接

4. 将title、posters、清晰度和mp4地址组装

### Vimeo

1. WebViewClient.onLoadResource过滤包含"https://www.dailymotion.com/player/metadata/video"的链接
2. 请求该[链接](https://player.vimeo.com/video/658304252/config?autopause=0&background=0&byline=0&bypass_privacy=1&context=Vimeo%5CController%5CMobile%5CClipController.main&controls=1&force_embed=0&h=eec134d822&logo=1&loop=0&outro_new=0&playbar=1&portrait=0&share=1&title=0&s=4ddb54d0df17c1782fc186a52d80def2f8eaad38_1641233865)，获取配置（vimeo_configs.json）

解析video字段及其下的title、thumbs字段，request/files/progressive

