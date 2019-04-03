<template>
    <b-form class="s-document-form">
        <b-form-group
                label="Заголовок"
                label-for="titleInput">
            <b-form-input
                    id="titleInput"
                    :state="titleState"
                    v-model="document.title"
                    required :readonly="!editMode" :plaintext="!editMode"  />
            <b-form-invalid-feedback>
                Заголовок не может быть пустой
            </b-form-invalid-feedback>
        </b-form-group>

        <b-form-group label="Вид документа" label-for="exampleInput2">
            <b-form-input
                    v-if="!editMode"
                    id="documentKindInput"
                    v-model="document.documentKind"
                    required :readonly="!editMode" :plaintext="!editMode"  />
            <s-select v-if="editMode" id="documentKindInput" :config="testSelectConfig" @value="(val) => document.documentKind=val" :value="document.documentKind"></s-select>
        </b-form-group>

    </b-form>
</template>

<style>
    .s-document-form {
        margin: 1em;
    }
</style>

<script>
    import SSelect from "../components/Select";

    export default {
        components: {SSelect},

        mounted() {
            this.testSelectConfig = {
                maxItems: 1,
                //plugins: ['remove_button'],
                valueField: 'title',
                labelField: 'title',
                searchField: 'title',
                preload: true,
                options: [{title: 'Акт'},
                    {title: 'Апелляционная жалоба'},
                    {title: 'Жалоба'},
                    {title: 'Запрос'},
                    {title: 'Заявка'},
                    {title: 'Извещение'},
                    {title: 'Исковое заявление'},
                    {title: 'Исполнительный лист'},
                    {title: 'Кассационная жалоба'},
                    {title: 'Определение'},
                    {title: 'Ответ на запрос'},
                    {title: 'Отчет'},
                    {title: 'Письмо'},
                    {title: 'Повестка'},
                    {title: 'Поручение'},
                    {title: 'Постановление'},
                    {title: 'Предписание'},
                    {title: 'Представление'},
                    {title: 'Предупреждение'},
                    {title: 'Претензия'},
                    {title: 'Приглашение'},
                    {title: 'Приговор суда'},
                    {title: 'Приказ'},
                    {title: 'Протокол'},
                    {title: 'Распоряжение'},
                    {title: 'Решение'},
                    {title: 'Справка'},
                    {title: 'Судебная повестка'},
                    {title: 'Телеграмма'},
                    {title: 'Телефонограмма'},
                    {title: 'Требование'},
                    {title: 'Уведомление'},
                    {title: 'Указ'},
                    {title: 'Указание Минтранса'}],
            };
        },
        props: {
            value: {},
            editMode: {}
        },
        computed: {
            titleState() {
                return this.document.title.length > 0 ? null : false;
            },
            document: {
                get() {
                    return this.value;
                },
                set(v) {
                    this.$emit('input', v)
                }
            }
        },
        methods: {
            getFormState() {
                return !(this.titleState ===  false);
            }
        },
        data() {
            return {
                testSelectConfig: null
            }
        }
    }
</script>