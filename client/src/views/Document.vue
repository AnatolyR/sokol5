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
                <b-button v-if="!editMode && canEdit" :disabled="tab !== 'attributes'" @click="edit" size="sm">Редактировать</b-button>
                <b-button v-if="editMode" :disabled="tab !== 'attributes'" variant="success" @click="save" size="sm">Сохранить</b-button>
                <b-button v-if="editMode" :disabled="tab !== 'attributes'" variant="danger" @click="cancel" size="sm">Отменить</b-button>

                <b-button v-if="!editMode" v-for="a in actions" @click="execute(a)" size="sm">{{a.title}}</b-button>
            </div>


            <!-- HEADER -->
            <h5 class="s-document-header">{{document.documentType}} № {{document.documentNumber}} от {{document.registrationDateStr}} ({{document.documentKind}})</h5>
            <h4 class="s-document-subheader">{{document.title}}</h4>
            <span class="s-document-status">Статус: {{document.status}}</span>


            <!-- TABS -->
            <ul class="s-document-tabs nav nav-tabs" id="myTab" role="tablist">
                <li class="nav-item" v-if="taskId">
                    <a class="nav-link"
                       v-bind:class="{ active: tab === 'task' }"
                       data-toggle="tab"
                       href="#"
                       @click.prevent="showTask">
                        Задача
                    </a>
                </li>
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
                        Исполнение <b-badge v-if="processCount > 0" style="top: -0.2em; position: relative;" variant="light">{{processCount}}</b-badge>
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

            <s-task-form v-if="tab === 'task' && taskId" :task-id="taskId"></s-task-form>

            <!-- FORM -->
            <!--<s-document-form ref="attributesForm" v-if="tab === 'attributes'" @formState="(val) => this.formState = val"-->
                             <!--:edit-mode="editMode" v-model="document"-->
                             <!--:fieldsLevels="fieldsLevels"/>-->

            <s-document-incoming-form v-if="tab === 'attributes' && document.documentType === 'Тестовый'"
                                      ref="attributesForm"
                                      @formState="(val) => this.formState = val"
                                      :edit-mode="editMode"
                                      v-model="document"
                                      :fieldsLevels="fieldsLevels"/>

            <s-document-incoming-form v-if="tab === 'attributes' && document.documentType === 'Входящий'"
                                      ref="attributesForm"
                                      @formState="(val) => this.formState = val"
                                      :edit-mode="editMode"
                                      v-model="document"
                                      :dictionariesConfigs="dictionariesConfigs"
                                      :fieldsLevels="fieldsLevels"/>

            <s-document-inner-form v-if="tab === 'attributes' && document.documentType === 'Внутренний'"
                                   ref="attributesForm"
                                   @formState="(val) => this.formState = val"
                                   :edit-mode="editMode"
                                   v-model="document"
                                   :dictionariesConfigs="dictionariesConfigs"
                                   :fieldsLevels="fieldsLevels"/>

            <b-modal id="execution-modal" size="lg" ref="execution-modal" title="Создание поручения">
                <p class="my-4">
                    <s-execution-form ref="execution-form" :document-id="documentId" :action-id="form"></s-execution-form>
                </p>
                <template slot="modal-footer" slot-scope="{ ok, cancel, hide }">
                    <b-form-checkbox v-model="addOneMore"
                    >
                        Добавить еще одно поручение
                    </b-form-checkbox>
                    <b-button size="sm" variant="light" @click="saveExecutionForm(cancel)">
                        Отмена
                    </b-button>
                    <b-button size="sm" variant="success" @click="saveExecutionForm()">
                        Сохранить
                    </b-button>
                </template>
            </b-modal>

            <b-modal id="executionReport-modal" size="lg" ref="executionReport-modal" title="Создание отчета об исполнении">
                <p class="my-4">
                    <s-execution-report-form ref="executionReport-form" :document-id="documentId" :action-id="form"></s-execution-report-form>
                </p>
                <template slot="modal-footer" slot-scope="{ ok, cancel, hide }">
                    <b-button size="sm" variant="light" @click="cancel()">
                        Отмена
                    </b-button>
                    <b-button size="sm" variant="success" @click="saveExecutionReportForm()">
                        Сохранить
                    </b-button>
                </template>
            </b-modal>

            <b-modal id="document-info-modal" ref="document-info-modal" modal-class="s-modal-incorrect-fields" title="Некорректно заполненные поля">
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
                <s-attach-form :object-id="documentId" :object-type="'document'" @updateAttaches="updateAttachCount"></s-attach-form>
            </div>
            <div v-if="tab === 'process'">
                <s-process-form :object-id="documentId" :object-type="'document'"></s-process-form>
            </div>
            <div v-if="tab === 'links'">
                <s-linked-documents-form :object-id="documentId" :object-type="'document'" @updateLinks="updateLinksCount"></s-linked-documents-form>
            </div>
            <div v-if="tab === 'history'">
                <s-history-form :object-id="documentId" :object-type="'document'"></s-history-form>
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
    import SHistoryForm from "../components/HistoryForm";
    import SProcessForm from "../components/ProcessForm";
    import SLinkedDocumentsForm from "../components/LinkedDocumentsForm";
    import SExecutionForm from "../components/ExecutionForm";
    import SExecutionReportForm from "../components/ExecutionReportForm";
    import STaskForm from '../components/TaskForm';

    import {UserSelectConfig} from "../configs/UserSelectConfig";
    import {DocumentKindConfig} from "../configs/DocumentKindConfig";
    import {AddresseeCopiesSelectConfig} from "../configs/AddresseeCopiesSelectConfig";
    import {ExternalOrganizationConfig} from "../configs/ExternalOrganizationConfig";
    import {ExternalOrganizationPersonConfig} from "../configs/ExternalOrganizationPersonConfig";
    import {DeliveryMethodConfig} from "../configs/DeliveryMethodConfig";

    export default {
        // components: {SSelect, SDocumentForm},
        components: {SExecutionForm, SSelect, SDocumentIncomingForm,
            SDocumentInnerForm, SAttachForm, SHistoryForm,
            SProcessForm, STaskForm, SExecutionReportForm,
            SLinkedDocumentsForm},
        mounted() {
            if (this.$route.meta.openAsTask === true) {
                this.tab = 'task';
            }
            this.loadDocument();
        },
        props: [
            "documentId",
            "taskId"
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
            loadActions() {
                axios.get(`/api/document/${this.documentId}/actions`)
                    .then((res) => {
                        this.actions = res.data.filter(a => a.id !== "edit");
                        this.canEdit = res.data.findIndex(a => a.id === "edit") >= 0;
                    });
            },
            execute(action) {
                if (action.form) {
                    this.showActionForm(action.form);
                    return;
                }
                let actionId = action.id;
                axios.post(`/api/document/${this.documentId}/actions/${actionId}`, {}).then(() => {
                    this.$bvToast.toast(`Статус обновлен`, {
                        variant: 'success',
                        solid: true,
                        autoHideDelay: 2000
                    });
                    this.loadDocument();
                }).catch((err) => {
                    console.log('err', err);
                    this.errorMessage = 'Не удается изменить статус';
                });
            },
            showActionForm(form) {
                this.form = form;
                this.$refs[`${form}-modal`].show();
            },
            saveExecutionForm(cancel) {
                if (cancel) {
                    cancel();
                    if (this.doLoadDocument) {
                        this.loadDocument();
                        this.doLoadDocument = false;
                    }
                    return;
                }
                let executionForm = this.$refs[`${this.form}-form`];
                let executionData = executionForm.getData();
                executionData.actionId = this.actions.find(a => a.form === this.form).id;
                executionForm.resetExecutor();
                axios.post(`/api/document/${this.documentId}/actions`, executionData).then(() => {
                    this.$bvToast.toast(`Статус обновлен`, {
                        variant: 'success',
                        solid: true,
                        autoHideDelay: 2000
                    });
                    this.doLoadDocument = true;
                }).catch((err) => {
                    console.log('err', err);
                    this.errorMessage = 'Не удается изменить статус';
                });
                if (!this.addOneMore) {
                    this.$refs[`${this.form}-modal`].hide();
                    this.form = null;
                    if (this.doLoadDocument) {
                        this.loadDocument();
                        this.doLoadDocument = false;
                    }
                }
            },
            saveExecutionReportForm() {
                let executionData = this.$refs[`${this.form}-form`].getData();
                executionData.actionId = this.actions.find(a => a.form === this.form).id;

                axios.post(`/api/document/${this.documentId}/actions`, executionData).then(() => {
                    this.$bvToast.toast(`Статус обновлен`, {
                        variant: 'success',
                        solid: true,
                        autoHideDelay: 2000
                    });
                    this.loadDocument();
                }).catch((err) => {
                    console.log('err', err);
                    this.errorMessage = 'Не удается изменить статус';
                });

                this.$refs[`${this.form}-modal`].hide();
                this.form = null;
            },
            updateAttachCount() {
                axios.get(`/api/attaches/search/countByObjectId?objectId=${this.documentId}&objectType=document`).then((res) => {
                    this.attachesCount = res.data;
                });
            },
            updateLinksCount() {
                axios.get(`/api/links/search/countByObjectId?objectId=${this.documentId}&objectType=document`).then((res) => {
                    this.linksCount = res.data;
                });
            },
            goBack() {
                this.$router.go(-1);
            },
            edit() {
                this.editMode = true;
            },
            cancel() {
                if (this.$route && this.$route.params && this.$route.params.isNew === true) {
                    this.$route.params.isNew = false;
                }
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
                    this.$refs['document-info-modal'].show();
                    // this.errorMessage = "Некорректно заполненные поля";
                    return;
                } else {
                    this.errorMessage = null;
                }

                const document = JSON.parse(JSON.stringify(this.document));

                this.fieldsDescriptions.forEach((f) => {
                    if (f.type === 'Instant') {
                        if (document[f.name]) {
                            document[f.name] = new Date(document[f.name]).toISOString();
                        }
                    }
                });
                this.document.type = this.document.documentType;
                let saveUrl = '/api/document';

                axios.post(saveUrl, document).then(() => {
                    // this.successMessage = 'Документ сохранен';
                    this.$bvToast.toast(`Документ сохранен`, {
                        variant: 'success',
                        solid: true,
                        autoHideDelay: 2000
                    });
                    // setTimeout(() => this.successMessage = null, 3000);
                    if (this.$route && this.$route.params && this.$route.params.isNew === true) {
                        this.$route.params.isNew = false;
                    }
                    this.loadDocument();
                }).catch((err) => {
                    console.log('err', err);
                    this.errorMessage = 'Не удается сохранить документ';
                });
            },
            loadDocument() {
                this.tab = "attributes";
                this.updateAttachCount();
                this.updateLinksCount();
                this.loadActions();

                if (this.$route.params.isNew === true) {
                    this.editMode = true;
                } else {
                    this.editMode = false;
                }
                this.errorMessage = null;
                this.loading = true;
                axios.get(`/api/document/${this.documentId}`).then((response) => {
                    const document = response.data.document;
                    const fieldsLevels = response.data.fields;
                    const fieldsDescriptions = response.data.fieldsDescriptions;
                    this.fieldsLevels = fieldsLevels;
                    this.fieldsDescriptions = fieldsDescriptions;

                    fieldsDescriptions.forEach((f) => {
                       if (f.type === 'Instant') {
                           try {
                               const date = Date.parse(document[f.name]);
                               if (!isNaN(date)) {
                                   const dateStr = new Date(date).toLocaleDateString("ru-RU");
                                   document[f.name] = new Date(date);
                                   document[f.name + 'Str'] = dateStr;
                               }
                           } catch (e) {

                           }
                       }
                    });

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
            showTask() {
                this.tab = 'task';
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
                doLoadDocument: false,

                attachesCount: "",
                processCount: "",
                linksCount: "",

                formState: null,
                uncorrectFields: "",
                actions: [],
                form: null,

                canEdit: false,
                addOneMore: false,

                dictionariesConfigs: {
                    userSelectConfig: UserSelectConfig(),
                    documentKindConfig: DocumentKindConfig(),
                    addresseeCopiesSelectConfig: AddresseeCopiesSelectConfig(),
                    externalOrganizationConfig: ExternalOrganizationConfig(),
                    externalOrganizationPersonConfig: ExternalOrganizationPersonConfig(),
                    deliveryMethodConfig: DeliveryMethodConfig()
                }
            }
        }
    }
</script>