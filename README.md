# Footfall

Fabric (1.21.1, 1.21.x hedefli) tamamen client-side ayak izi modu.

## Özellikler
- Kar, kum, kırmızı kum, çamur, soul sand/soil, moss ve tozlu bloklarda otomatik ayak izi
- Sağ/sol ayak dönüşümü, oyuncunun baktığı yöne göre otomatik döndürme
- Vanilla'nın kendi adım-sesi tetiğine (mixin) bağlı olduğu için yürüyüş animasyonuyla senkron
- Zamanla solma; yağmurda daha hızlı silinme, kar yağışında örtülme, suda anında temizlenme
- Oyuncu + opsiyonel mob desteği (kurt, tilki, kedi, at ailesi, inek, koyun, domuz, tavuk, deve, llama, demir golem, warden)
- Mesafe + frustum culling ile GPU dostu tek seferde batch render
- Mod Menu destekli ayarlar ekranı (Cloth Config gibi harici bağımlılık gerekmez)

## Derleme (GitHub Actions)
`main`/`master` dalına push at, `.github/workflows/build.yml` otomatik olarak
Java 21 kurar, `gradle-wrapper.jar`'ı yeniden üretir (bu jar binary olduğu için
repoya eklenmedi — CI her build'de `gradle wrapper` ile tazeler), `./gradlew build`
çalıştırır ve oluşan `.jar`'ı Artifact olarak yükler.

Yerelde derlemek isterse:
```
gradle wrapper --gradle-version 8.10.2
./gradlew build
```

## Notlar
- Bu ortamda internet erişimi olmadığı için Fabric/Yarn/Loom bağımlılıkları
  indirilip gerçek bir derleme testi yapılamadı. İlk CI çalıştırmasında
  mapping/method isimlerinde küçük uyuşmazlıklar çıkarsa (özellikle
  `FootprintRenderLayer` içindeki `RenderLayer` alan adları ve
  `playStepSound` mixin imzası), CI log'unu paylaş, birlikte düzeltelim —
  EclipseHollowWatcher'daki refmap hatasında yaptığımız gibi.
- Doku tek bir yumuşak-kenarlı ayak izi sprite'ı olup yüzeye göre renklendirilir
  (vertex tint). İstersen her yüzey için ayrı, elle çizilmiş dokularla
  değiştirilebilir.
