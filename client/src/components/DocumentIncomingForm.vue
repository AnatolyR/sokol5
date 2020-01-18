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
            fieldsLevels: {}
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
                        documentKindConfig: this.documentKindConfig,
                        addresseeConfig: this.userSelectConfig,
                        addresseeCopiesConfig: this.addresseeCopiesSelectConfig,
                        externalOrganizationConfig: this.externalOrganizationConfig,
                        externalSignerConfig: this.externalOrganizationPersonConfig,
                        externalExecutorConfig: this.externalOrganizationPersonConfig,
                        registrarConfig: this.userSelectConfig,
                        executorConfig: this.userSelectConfig,
                        controllerConfig: this.userSelectConfig,
                        deliveryMethodConfig: this.deliveryMethodConfig
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
                userSelectConfig: {
                    maxItems: 1,
                    //plugins: ['remove_button'],
                    valueField: 'id',
                    labelField: 'title',
                    searchField: 'title',
                    preload: true,
                    load(query, callback) {
                        if (!query.length) {
                            return callback();
                        }
                        axios.get(`/api/users/search/userByTitle?title=%25${query}%25`).then((res) => {
                            const users = res.data._embedded.users;
                            callback(users);
                        }).catch(() => {
                            callback();
                        })
                    }
                },

                addresseeCopiesSelectConfig: {
                    maxItems: 100,
                    plugins: ['restore_on_backspace', 'remove_button'],
                    valueField: 'id',
                    labelField: 'title',
                    searchField: 'title',
                    create: false,
                    closeAfterSelect: true,
                    load(query, callback) {
                        if (!query.length) {
                            return callback();
                        }
                        axios.get(`/api/users/search/userByTitle?title=%25${query}%25`).then((res) => {
                            const users = res.data._embedded.users;
                            callback(users);
                        }).catch(() => {
                            callback();
                        })
                    }
                },
                dateConfig: {
                    locale:'ru'
                },

                externalOrganizationConfig: {
                    maxItems: 1,
                    //plugins: ['remove_button'],
                    valueField: 'id',
                    labelField: 'title',
                    searchField: 'title',
                    preload: true,
                    load(query, callback) {
                        if (!query.length) {
                            return callback();
                        }
                        axios.get(`/api/contragents/search/userByTitle?title=%25${query}%25`).then((res) => {
                            const users = res.data._embedded.contragents;
                            callback(users);
                        }).catch(() => {
                            callback();
                        })
                    }
                },

                externalOrganizationPersonConfig: {
                    maxItems: 1,
                    //plugins: ['remove_button'],
                    valueField: 'title',
                    labelField: 'title',
                    searchField: 'title',
                    preload: true,
                    create: true,
                    load(query, callback) {
                        if (!query.length) {
                            return callback();
                        }
                        axios.get(`/api/contragentpersons/search/personByTitle?organizationId=${this.depends.externalOrganization}&title=%25${query}%25`).then((res) => {
                            const users = res.data._embedded.contragentPersons;
                            callback(users);
                        }).catch(() => {
                            callback();
                        })
                    }
                },

                deliveryMethodConfig: {
                    maxItems: 1,
                    //plugins: ['remove_button'],
                    valueField: 'title',
                    labelField: 'title',
                    searchField: 'title',
                    preload: true,
                    // create: true,
                    load(query, callback) {
                        // if (!query.length) {
                        //     return callback();
                        // }
                        // axios.get(`/api/deliveryMethods/search/methodByTitle?title=%25${query}%25`).then((res) => {
                        axios.get(`/api/deliveryMethods?size=100`).then((res) => {
                            this.settings.load = null;
                            const users = res.data._embedded.deliveryMethods;
                            callback(users);
                        }).catch(() => {
                            callback();
                        })
                    }
                },
                documentKindConfig: {
                    maxItems: 1,
                    //plugins: ['remove_button'],
                    valueField: 'title',
                    labelField: 'title',
                    searchField: 'title',
                    preload: true,
                    // create: true,
                    load(query, callback) {
                        axios.get(`/api/documentKinds?size=100`).then((res) => {
                            this.settings.load = null;
                            const users = res.data._embedded.documentKinds;
                            callback(users);
                        }).catch(() => {
                            callback();
                        })
                    }
                }

            }
        }
    }
</script>