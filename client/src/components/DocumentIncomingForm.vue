<template>
    <b-form class="s-document-form">
        <s-input-field name="title" title="Заголовок" />

        <s-input-field name="comment" title="Комментарий" />

        <b-row>
            <b-col>
                <s-select-field name="documentKind" title="Вид документа" />
            </b-col>

            <b-col>
                <s-select-field name="addressee" title="Адресат" />
            </b-col>
        </b-row>

        <s-multi-select-field name="addresseeCopies" title="Адресаты (копии)" />

        <div class="s-document-block-delimiter"></div>

        <s-select-field name="externalOrganization" title="Корреспондент" />

        <b-row>
            <b-col>
                <s-select-field name="externalSigner" title="Кем подписано" />
            </b-col>
            <b-col>
                <s-select-field name="externalExecutor" title="Исполнитель (исх)" />
            </b-col>
        </b-row>

        <b-row>
            <b-col>
                <s-input-field name="externalNumber" title="Исходящий номер" />
            </b-col>
            <b-col>
                <s-date-field name="externalDate" title="Исходящая дата" />
            </b-col>
        </b-row>

        <div class="s-document-block-delimiter"></div>

        <b-row>
            <b-col>
                <s-input-field name="documentNumber" title="Номер документа" />
            </b-col>
            <b-col>
                <s-input-field name="caseNumber" title="Дело" />
            </b-col>
        </b-row>

        <b-row>
            <b-col>
                <s-input-field name="pageCount" title="Количество листов" />
            </b-col>
            <b-col>
                <s-input-field name="appendixCount" title="Количество приложений" />
            </b-col>
        </b-row>

        <div class="s-document-block-delimiter"></div>

        <b-row>
            <b-col>
                <s-date-field name="registrationDate" title="Дата регистрации" />
            </b-col>
            <b-col>
                <s-select-field name="registrar" title="Регистратор" />
            </b-col>
        </b-row>

        <b-row>
            <b-col>
                <s-date-field name="executionDate" title="Дата исполнения" />
            </b-col>
            <b-col>
                <s-select-field name="executor" title="Исполнитель" />
            </b-col>
        </b-row>

        <b-row>
            <b-col>
                <s-select-field name="controller" title="Контроллер" />
            </b-col>
            <b-col>
                <s-select-field name="deliveryMethod" title="Способ доставки" />
            </b-col>
        </b-row>

    </b-form>
</template>

<style>
    .s-document-form {
        margin: 1em;
    }

    .s-document-block-delimiter {
        border-top: 1px solid #dee2e6;
        height: 2em;
    }

    input.form-control-plaintext {
        padding-top: 0;
        padding-bottom: 0;
    }

    .s-invalid-field .selectize-input {
        border-color: #dc3545 !important;
    }
</style>

<script>
    import SSelect from "../components/fields/Select";
    import SSelectGroup from "../components/fields/SelectGroup";
    import SSelectField from "../components/fields/SelectField";
    import SMultiSelectGroup from "../components/fields/MultiSelectGroup";
    import SMultiSelect from "../components/fields/MultiSelect";
    import SMultiSelectField from "../components/fields/MultiSelectField";
    import SInputGroup from "../components/fields/InputGroup"
    import SInputField from "../components/fields/InputField"
    import SDateGroup from "../components/fields/DateGroup"
    import SDateField from "../components/fields/DateField"
    import axios from 'axios';

    import datePicker from 'vue-bootstrap-datetimepicker';

    export default {
        components: {SSelect, SMultiSelect, SMultiSelectField, datePicker,
            SSelectGroup, SSelectField, SInputGroup, SInputField, SMultiSelectGroup,
            SDateGroup, SDateField},

        mounted() {
            this.showState = false;
        },
        props: {
            value: {},
            editMode: {},
            fieldsLevels: {},
            dictionariesConfigs: {}
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
            },
            dataSource: {
                get() {
                    return {
                        value: this.value,
                        editMode: this.editMode,
                        fieldsLevels: this.fieldsLevels,
                        document: this.document,
                        state: this.state.bind(this),
                        documentKindConfig: this.dictionariesConfigs.documentKindConfig,
                        addresseeConfig: this.dictionariesConfigs.userSelectConfig,
                        addresseeCopiesConfig: this.dictionariesConfigs.addresseeCopiesSelectConfig,
                        externalOrganizationConfig: this.dictionariesConfigs.externalOrganizationConfig,
                        externalSignerConfig: this.dictionariesConfigs.externalOrganizationPersonConfig,
                        externalExecutorConfig: this.dictionariesConfigs.externalOrganizationPersonConfig,
                        registrarConfig: this.dictionariesConfigs.userSelectConfig,
                        executorConfig: this.dictionariesConfigs.userSelectConfig,
                        controllerConfig: this.dictionariesConfigs.userSelectConfig,
                        deliveryMethodConfig: this.dictionariesConfigs.deliveryMethodConfig
                    }
                }
            }
        },
        methods: {
            getFormState() {
                let result = [];
                this.showState = true;
                for (let field in this.document) {
                    let state = this.state(field);
                    if (state !== null) {
                        result.push(this.fieldTitles[field]);
                    }
                }

                return result;
            },
            state(field) {
                if (!this.showState) {
                    return null;
                }

                if (field === 'title') {
                    return this.document.title && this.document.title.length > 0 ? null : false;
                }

                if (field === 'pageCount' || field === 'appendixCount') {
                    if (this.document[field]) {
                        if (!(this.isNormalInteger(this.document[field]) && this.document[field] >= 0)) {
                            return false;
                        }
                    } else if (this.fieldsLevels[field] === '3') {
                        return false;
                    }
                    return null;
                }

                if (this.fieldsLevels[field]) {
                    return (this.fieldsLevels[field] === '3' && !this.document[field]) ? false : null;
                } else {
                    return (this.fieldsLevels['*'] === '3' && !this.document[field]) ? false : null;
                }
            },
            isNormalInteger(str) {
                return /^\+?(0|[1-9]\d*)$/.test(str);
            }
        },
        data() {
            const documentData = this.document;
            return {
                showState: false,
                fieldTitles: {
                    addressee: "Адресат",
                    addresseeCopies: "Адресаты (копии)",
                    appendixCount: "Количество приложений",
                    caseNumber: "Дело",
                    comment: "Комментарий",
                    controller: "Контроллер",
                    deliveryMethod: "Способ доставки",
                    documentKind: "Вид документа",
                    documentNumber: "Номер документа",
                    documentType: "Тестовый",
                    executionDate: "Дата исполнения",
                    executor: "Исполнитель",
                    externalDate: "Исходящая дата",
                    externalExecutor: "Исполнитель",
                    externalNumber: "Исходящий номер",
                    externalOrganization: "Корреспондент",
                    externalSigner: "Кем подписано",
                    pageCount: "Количество листов",
                    registrar: "Регистратор",
                    registrationDate: "Дата регистрации",
                    title: "Заголовок"
                },

                dateConfig: {
                    locale:'ru'
                }

            }
        }
    }
</script>