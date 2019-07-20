<template>
    <div class="s-document-container">
        <div v-if="errorMessage" class="">
            <b-alert show variant="danger">{{errorMessage}}</b-alert>
        </div>
        <div v-if="successMessage" class="">
            <b-alert show variant="success">{{successMessage}}</b-alert>
        </div>
        <div v-if="loading" class="">
            <b-spinner variant="secondary" label="Загрузка..." />
        </div>
        <div v-if="!loading && document" class="">
            <!-- BUTTONS -->
            <div class="s-document-button-bar">
                <b-button v-if="openedFromFolder" size="sm" variant="light" @click="back"><font-awesome-icon icon="angle-left" /> Назад</b-button>
                <b-button v-if="!editMode" @click="edit" size="sm">Редактировать</b-button>
                <b-button v-if="editMode" variant="success" @click="save" size="sm">Сохранить</b-button>
                <b-button v-if="editMode" variant="danger" @click="cancel" size="sm">Отменить</b-button>
                <b-button size="sm">Подписать</b-button>
                <b-button size="sm">Согласовать</b-button>
            </div>


            <!-- HEADER -->
            <h5 class="s-document-header">{{document.documentType}} № {{document.documentNumber}} от {{document.registrationDateStr}} ({{document.documentKind}})</h5>
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

            <!-- FORM -->
            <!--<s-document-form ref="attributesForm" v-if="tab === 'attributes'" @formState="(val) => this.formState = val"-->
                             <!--:edit-mode="editMode" v-model="document"-->
                             <!--:fieldsLevels="fieldsLevels"/>-->

            <s-document-incoming-form v-if="tab === 'attributes' && document.documentType === 'Тестовый'" ref="attributesForm"
                                      @formState="(val) => this.formState = val"
                                      :edit-mode="editMode" v-model="document"
                                      :fieldsLevels="fieldsLevels"/>

            <s-document-incoming-form v-if="tab === 'attributes' && document.documentType === 'Входящий'" ref="attributesForm"
                                      @formState="(val) => this.formState = val"
                             :edit-mode="editMode" v-model="document"
                             :fieldsLevels="fieldsLevels"/>

            <s-document-inner-form v-if="tab === 'attributes' && document.documentType === 'Внутренний'" ref="attributesForm"
                                   @formState="(val) => this.formState = val"
                             :edit-mode="editMode" v-model="document"
                             :fieldsLevels="fieldsLevels"/>

            <b-modal id="info-modal" ref="info-modal" title="Некорректно заполненные поля">
                <p class="my-4">
                    {{uncorrectFields}}
                </p>
                <template slot="modal-footer" slot-scope="{ ok, cancel, hide }">
                    <b-button size="sm" variant="info" @click="cancel()">
                        Ок
                    </b-button>
                </template>
            </b-modal>

            <div v-if="tab === 'attaches'">
                <s-attach-form :object-id="documentId" :object-type="'document'"></s-attach-form>
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

    .s-document-button-bar button {
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
    import SSelect from "../components/fields/Select";
    // import SDocumentForm from "../components/DocumentForm";
    import SDocumentIncomingForm from "../components/DocumentIncomingForm";
    import SDocumentInnerForm from "../components/DocumentInnerForm";
    import SAttachForm from "../components/AttachForm";

    export default {
        // components: {SSelect, SDocumentForm},
        components: {SSelect, SDocumentIncomingForm, SDocumentInnerForm, SAttachForm},
        mounted() {
            this.loadDocument();
            axios.get(`/api/attaches/search/countByObjectId?objectId=${this.documentId}&objectType=document`).then((res) => {
                this.attachesCount = res.data;
            })
        },
        props: [
            "documentId"
        ],
        computed: {
            openedFromFolder() {
                return this.$route.meta.openedFromFolder === true;
            }
        },
        watch: {
            documentId () {
                this.loadDocument();
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
                this.errorMessage = null;
                this.document = JSON.parse(this.documentBackup);
                this.editMode = false;
            },
            back() {
                if (this.editMode) {
                    this.cancel();
                }
                this.$router.go(-1);
            },
            save() {
                this.errorMessage = null;
                let fields = this.$refs.attributesForm && this.$refs.attributesForm.getFormState();
                if (fields && fields.length > 0) {
                    this.uncorrectFields = fields.join(", ");
                    this.$refs['info-modal'].show();
                    // this.errorMessage = "Некорректно заполненные поля";
                    return;
                } else {
                    this.errorMessage = null;
                }

                const document = JSON.parse(JSON.stringify(this.document));
                if (document.registrationDate) {
                    document.registrationDate = new Date(document.registrationDate).toISOString();
                }
                if (document.externalDate) {
                    document.externalDate = new Date(document.externalDate).toISOString();
                }
                if (document.executionDate) {
                    document.executionDate = new Date(document.executionDate).toISOString();
                }

                let saveUrl;
                if (this.document.documentType === "Входящий") {
                    saveUrl = '/api/incomingDocument';
                } else if (this.document.documentType === "Исходящий") {
                    saveUrl = '/api/outgoingDocument';
                } else if (this.document.documentType === "Внутренний") {
                    saveUrl = '/api/innerDocument';
                }

                axios.post(saveUrl, document).then(() => {
                    // this.successMessage = 'Документ сохранен';
                    this.$bvToast.toast(`Документ сохранен`, {
                        variant: 'success',
                        solid: true,
                        autoHideDelay: 2000
                    });
                    // setTimeout(() => this.successMessage = null, 3000);
                    this.loadDocument();
                }).catch((err) => {
                    console.log('err', err);
                    this.errorMessage = 'Не удается сохранить документ';
                });
            },
            loadDocument() {
                this.editMode = false;
                this.errorMessage = null;
                this.loading = true;
                axios.get(`/api/document/${this.documentId}`).then((response) => {
                    const document = response.data.document;
                    const fieldsLevels = response.data.fields;
                    this.fieldsLevels = fieldsLevels;

                    try {
                        const registrationDate = Date.parse(document.registrationDate);
                        if (!isNaN(registrationDate)) {
                            const registrationDateStr = new Date(registrationDate).toLocaleDateString("ru-RU");
                            document.registrationDate = new Date(registrationDate);
                            document.registrationDateStr = registrationDateStr;
                        }
                    } catch (e) {

                    }
                    try {
                        const externalDate = Date.parse(document.externalDate);
                        if (!isNaN(externalDate)) {
                            const externalDateStr = new Date(externalDate).toLocaleDateString("ru-RU");
                            document.externalDate = new Date(externalDate);
                            document.externalDateStr = externalDateStr;
                        }
                    } catch (e) {

                    }
                    try {
                        const executionDate = Date.parse(document.executionDate);
                        if (!isNaN(executionDate)) {
                            const executionDateStr = new Date(executionDate).toLocaleDateString("ru-RU");
                            document.executionDate = new Date(executionDate);
                            document.executionDateStr = executionDateStr;
                        }
                    } catch (e) {

                    }
                    this.document = document;
                    this.documentBackup = JSON.stringify(document);
                    // if (this.document.status === "Черновик") {
                    //     this.editMode = true;
                    // }

                    this.loading = false;
                }).catch((error) => {
                    this.loading = false;
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
                fieldsLevels: null,

                tab: 'attributes',
                editMode: false,

                attachesCount: 0,
                processCount: 2,
                linksCount: 3,

                formState: null,
                uncorrectFields: ""
            }
        }
    }
</script>