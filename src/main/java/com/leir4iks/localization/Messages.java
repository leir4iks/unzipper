package com.leir4iks.localization;

public final class Messages {
    public static final String ERROR_FILE_NOT_FOUND = "Ошибка: Файл не найден: %s\n";
    public static final String ERROR_DEST_DIR_CREATION = "Ошибка: Не удалось создать директорию назначения: %s\n";
    public static final String ERROR_UNSUPPORTED_FORMAT = "Ошибка: Формат не поддерживается. Доступные форматы: .zip, .tar.gz, .rar, .7z, .tar.bz2\n";
    public static final String ERROR_EXTRACTION_FAILED = "Ошибка: Не удалось извлечь файлы: %s\n";
    public static final String ERROR_LISTING_FAILED = "Ошибка: Не удалось прочитать содержимое архива: %s\n";
    public static final String ERROR_NO_ARCHIVES_SPECIFIED = "Ошибка: Не указаны файлы архивов.\n";
    public static final String ERROR_TASK_FAILED = "Ошибка: Не удалось обработать %s: %s\n";

    public static final String INFO_EXTRACTION_STARTING = "Выполняется извлечение %s в потоке %s\n";
    public static final String INFO_LISTING_CONTENTS = "Содержимое архива %s:\n";

    public static final String SUCCESS_EXTRACTION_SINGLE = "Архив %s успешно распакован в: %s\n";
    public static final String SUCCESS_ALL_TASKS_COMPLETED = "Все задачи успешно выполнены.\n";

    private Messages() {}
}