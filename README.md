Необходимо создать Multimodule проект с двумя модулями:

- common (вспомогательные/общие классы, jar);

- service (основной функционал, jar, зависит от модуля common).

1. Переопределить основные плагины, чтобы везде использовались актуальные версии;

2. Должен быть настроен запуск Unit и Integration тестов (использовать JUnit 5);

3. Должен быть настроен jacoco plugin, который генерирует отчет после Integration тестов;

4. Для сборки проектов нужно использовать Maven wrapper.
