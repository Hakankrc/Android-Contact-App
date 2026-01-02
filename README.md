# 📱 Modern Android Contact App (Clean Architecture & Jetpack Compose)

Bu proje, modern Android geliştirme standartları kullanılarak geliştirilmiş, ölçeklenebilir ve test edilebilir bir rehber uygulamasıdır. **Case Study** kapsamında geliştirilmiş olup, **Clean Architecture**, **MVVM**, **Hilt**, **Retrofit** ve **Jetpack Compose** gibi güncel teknolojileri barındırır.

## 🚀 Özellikler

* **Rehber Listeleme:** API üzerinden çekilen kullanıcıların alfabetik (Sticky Header) olarak listelenmesi.
* **Detaylı Profil:** Palette API kullanılarak profil fotoğrafındaki baskın renge göre dinamik arka plan oluşturma.
* **CRUD İşlemleri:** Yeni kişi ekleme, mevcut kişiyi düzenleme ve silme.
* **Sola Kaydırma (Swipe-to-Action):** Listeden hızlıca silme ve düzenleme aksiyonları.
* **Cihaz Entegrasyonu:** Cihazın yerel rehberindeki numaralarla API verilerini eşleştirme ve "Cihazda Kayıtlı" ikonu gösterme.
* **Cihaza Kayıt:** Uygulama içindeki kişiyi telefonun kendi rehberine aktarma (Intent).
* **Akıllı Arama:** İsim, soyisim ve telefon numarasına göre (boşluk duyarlı) filtreleme ve arama geçmişi.

## 🛠️ Kullanılan Teknolojiler ve Kütüphaneler (Tech Stack)

Bu projede endüstri standardı kütüphaneler kullanılmıştır:

* **Dil:** [Kotlin](https://kotlinlang.org/)
* **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
* **Mimari:** Clean Architecture (Data, Domain, Presentation Layers) + MVVM
* **Dependency Injection:** [Hilt (Dagger)](https://dagger.dev/hilt/)
* **Network:** [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/)
* **Asenkron İşlemler:** [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & Flow
* **Görsel Yükleme:** [Coil](https://coil-kt.github.io/coil/)
* **Görsel İşleme:** [Palette API](https://developer.android.com/develop/ui/views/graphics/palette-colors) (Dinamik Renk Üretimi)
* **Animasyon:** [Lottie Files](https://lottiefiles.com/)
* **Yerel Veri Erişimi:** Android ContentResolver (Rehber İzni Yönetimi)

## 📂 Proje Mimarisi

Proje **Separation of Concerns (İlgi Alanlarının Ayrımı)** prensibine göre 3 ana katmana ayrılmıştır:

1.  **Domain Layer:** İş kurallarını (Use Cases, Repository Interfaces, Models) içerir. Saf Kotlin kodudur, Android framework'ünden bağımsızdır.
2.  **Data Layer:** Veri kaynaklarını (API, Database) yönetir. Repository implementasyonları buradadır.
3.  **Presentation Layer:** UI (Compose) ve State yönetimi (ViewModel) buradadır.



## 🔧 Kurulum ve API Key Yapılandırması

Bu proje özel bir API servisi kullanmaktadır. Çalıştırmak için kendi API Key'inize ihtiyacınız vardır.

1. Projeyi klonlayın:
   ```bash
   git clone [https://github.com/KULLANICI_ADIN/REPO_ADIN.git](https://github.com/KULLANICI_ADIN/REPO_ADIN.git)