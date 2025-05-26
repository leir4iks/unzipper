# 📦 Unzipper / Распаковщик

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Release](https://img.shields.io/github/v/release/leir4iks/unzipper)](https://github.com/leir4iks/unzipper/releases)

A powerful command-line archive extractor supporting multiple formats / Консольный распаковщик архивов с поддержкой множества форматов

---

## 🌟 Features / Возможности

### English
- **Multi-format support**: ZIP, TAR.GZ, and RAR archives
- **Flexible extraction**: Extract to current directory or specify custom destination
- **Auto-directory creation**: Creates destination directories if they don't exist
- **Security**: Built-in protection against zip-slip attacks
- **Interactive CLI**: User-friendly command-line interface
- **Path Traversal Protection**: Prevents extraction outside target directory
- **Input Validation**: Validates file paths and commands
- **Error Handling**: Graceful error handling with informative messages

### Русский
- **Поддержка форматов**: ZIP, TAR.GZ и RAR архивы
- **Гибкое извлечение**: Распаковка в текущую папку или указанную директорию
- **Автосоздание папок**: Создает папки назначения, если они не существуют
- **Безопасность**: Встроенная защита от zip-slip атак
- **Интерактивный CLI**: Удобный интерфейс командной строки
- **Защита от Path Traversal**: Предотвращает извлечение за пределы целевой папки
- **Валидация ввода**: Проверяет пути файлов и команды
- **Обработка ошибок**: Корректная обработка ошибок с информативными сообщениями

---

## 📥 Download / Скачать

**Latest Release / Последняя версия**: [Download here / Скачать здесь](https://github.com/leir4iks/unzipper/releases)

---

## 🚀 Usage / Использование

### Basic Commands / Основные команды

Extract to current directory / Распаковать в текущую папку:

```
unzip archive.zip
```

Extract to specific directory / Распаковать в указанную папку:

```
unzip archive.zip /path/to/destination
```

Supported formats / Поддерживаемые форматы:

```
unzip file.zip
unzip archive.tar.gz
unzip package.rar
```

Exit program / Выход из программы:

```
stop
```

### Examples / Примеры

#### English

```
unzip backup.zip
```
✅ Successfully extracted ZIP archive to: /current/directory

```
unzip data.tar.gz /home/user/extracted
```
✅ Successfully extracted TAR.GZ archive to: /home/user/extracted

```
unzip package.rar /tmp/files
```
✅ Successfully extracted RAR archive to: /tmp/files

#### Русский

```
unzip резервная_копия.zip
```
✅ Архив ZIP успешно распакован в: /текущая/папка

```
unzip данные.tar.gz /home/user/извлечено
```
✅ Архив TAR.GZ успешно распакован в: /home/user/извлечено

```
unzip пакет.rar /tmp/файлы
```
✅ Архив RAR успешно распакован в: /tmp/файлы

---

## 🐳 Pterodactyl Usage / Использование в Pterodactyl

### English
**When running unzipper on Pterodactyl panel, use paths starting with `home/container/`:**

Extract to server directory:

```
unzip backup.zip home/container/
```

Extract to plugins folder:

```
unzip plugins.zip home/container/plugins/
```

Extract to specific world folder:

```
unzip world.tar.gz home/container/worlds/survival/
```

### Русский
**При запуске распаковщика в панели Pterodactyl используйте пути, начинающиеся с `home/container/`:**

Распаковать в папку сервера:

```
unzip backup.zip home/container/
```

Распаковать в папку плагинов:
```
unzip plugins.zip home/container/plugins/
```

Распаковать в конкретную папку мира:
```
unzip world.tar.gz home/container/worlds/survival/
```

---

## ⚙️ Installation / Установка

### Download JAR / Скачать JAR

#### English
1. Download the latest JAR file from [releases](https://github.com/leir4iks/unzipper/releases)
2. Run with Java 17+:
```
java -jar unzipper-1.0.0.jar
```

#### Русский
1. Скачайте последний JAR файл из [релизов](https://github.com/leir4iks/unzipper/releases)
2. Запустите с Java 17+:
```
java -jar unzipper-1.0.0.jar
```

---

<div align="center">

**Made with ❤️ by leir4iks**

</div>
