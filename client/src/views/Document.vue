<template>
    <div class="s-document-container">
        <div v-if="errorMessage" class="">
            <b-alert show dismissible variant="danger">{{errorMessage}}</b-alert>
        </div>
        <div v-if="successMessage" class="">
            <b-alert :show="3" dismissible variant="success">{{successMessage}}</b-alert>
        </div>
        <div v-if="loading" class="">
            <b-spinner variant="secondary" label="Загрузка..." />
        </div>
        <div v-if="!loading" class="">
            <!-- BUTTONS -->
            <div class="s-document-button-bar">
                <b-button v-if="openedFromFolder" size="sm" variant="light" @click="$router.go(-1)"><font-awesome-icon icon="angle-left" /> Назад</b-button>
                <b-button v-if="!editMode" @click="edit" size="sm">Редактировать</b-button>
                <b-button v-if="editMode" variant="success" @click="save" size="sm">Сохранить</b-button>
                <b-button v-if="editMode" variant="danger" @click="cancel" size="sm">Отменить</b-button>
                <b-button size="sm">Подписать</b-button>
                <b-button size="sm">Согласовать</b-button>
            </div>


            <!-- HEADER -->
            <h5 class="s-document-header">{{document.documentType}} № {{document.documentNumber}} от {{document.registrationDate}} ({{document.documentKind}})</h5>
            <h4 class="s-document-subheader">{{document.title}}</h4>
            <span class="s-document-status">Статус: {{document.status}}</span>


            <!-- TABS -->
            <ul class="s-document-tabs nav nav-tabs" id="myTab" role="tablist">
                <li class="nav-item">
                    <a class="nav-link"
                       v-bind:class="{ active: tab === 'attributes' }"
                       data-toggle="tab"
                       href="#"
                       @click.prevent="showAttributes">
                        Реквизиты
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link"
                       v-bind:class="{ active: tab === 'attaches' }"
                       data-toggle="tab"
                       href="#"
                       @click.prevent="showAttaches">
                        Вложения <b-badge v-if="attachesCount > 0" style="top: -0.2em; position: relative;" variant="light">{{attachesCount}}</b-badge>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link"
                       v-bind:class="{ active: tab === 'process' }"
                       data-toggle="tab"
                       href="#"
                       @click.prevent="showProcess">
                        Процесс <b-badge v-if="processCount > 0" style="top: -0.2em; position: relative;" variant="light">{{processCount}}</b-badge>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link"
                       v-bind:class="{ active: tab === 'links' }"
                       data-toggle="tab"
                       href="#"
                       @click.prevent="showLinks">
                        Связи <b-badge v-if="linksCount > 0" style="top: -0.2em; position: relative;" variant="light">{{linksCount}}</b-badge>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link"
                       v-bind:class="{ active: tab === 'history' }"
                       data-toggle="tab"
                       href="#"
                       @click.prevent="showHistory">
                        История
                    </a>
                </li>
            </ul>


            <b-form v-if="tab === 'attributes'" class="s-document-form">
                <b-form-group
                        label="Заголовок"
                        label-for="titleInput">
                    <b-form-input
                            id="titleInput"
                            type="email"
                            v-model="document.title"
                            required :readonly="!editMode" :plaintext="!editMode"  />
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

            <div v-if="tab === 'attaches'">
                Вложения
            </div>
            <div v-if="tab === 'process'">
                Процесс
            </div>
            <div v-if="tab === 'links'">
                Связи
            </div>
            <div v-if="tab === 'history'">
                История
            </div>

        </div>
    </div>
</template>

<style>
    .s-document-button-bar {
        margin: 0.5em 0 1em 0;
    }
    .s-document-container {
        margin: 0.5em;
    }

    button {
        margin-right: 0.5em !important;
    }

    .s-document-tabs {
        margin-top: 0.5em;
    }

    h4.s-document-subheader {
        margin-bottom: 0.25em;
    }

    h5.s-document-header {
        margin-bottom: 0.25em;
    }

    .s-document-form label {
        margin-bottom: 0;
        font-weight: bold;
    }
    .s-document-form input {

    }

    .s-document-form {
        margin: 1em;
    }

</style>

<script>
    import axios from "axios";
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

            this.loadDocument();
        },
        props: [
            "documentId"
        ],
        computed: {
            openedFromFolder() {
                return this.$route.meta.openedFromFolder === true;
            }
        },
        methods: {
            goBack() {
                this.$router.go(-1);
            },
            edit() {
                this.editMode = true;
            },
            cancel() {
                this.document = JSON.parse(this.documentBackup);
                this.editMode = false;
            },
            save() {
                this.editMode = false;

                const document = JSON.parse(JSON.stringify(this.document));
                document.registrationDate = null;

                axios.post('/api/document', document).then(() => {
                    this.successMessage = 'Документ сохранен';
                    this.loadDocument();
                }).catch((err) => {
                    console.log('err', err);
                    this.errorMessage = 'Не удается сохранить документ';
                });
            },
            loadDocument() {
                this.loading = true;
                axios.get(`/api/document/${this.documentId}`).then((response) => {
                    const document = response.data;

                    try {
                        const registrationDate = new Date(Date.parse(document.registrationDate)).toLocaleDateString("ru-RU");
                        document.registrationDate = registrationDate;
                    } catch (e) {

                    }
                    this.document = document;
                    this.documentBackup = JSON.stringify(document);


                    this.loading = false;
                }).catch((error) => {
                    this.loading = false;
                    console.log(error);
                    this.errorMessage = 'Не удается загрузить данные';
                });
            },
            showAttributes() {
                this.tab = 'attributes';
            },
            showAttaches() {
                this.tab = 'attaches';
            },
            showProcess() {
                this.tab = 'process';
            },
            showLinks() {
                this.tab = 'links';
            },
            showHistory() {
                this.tab = 'history';
            }
        },
        data() {
            return {
                loading: true,
                errorMessage: null,
                successMessage: null,
                document: null,

                tab: 'attributes',
                editMode: false,

                testSelectConfig: null,

                attachesCount: 1,
                processCount: 2,
                linksCount: 3
            }
        }
    }
</script>