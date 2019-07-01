<template>
    <b-form class="s-document-form">
        <s-input-group
                title="Заголовок"
                id="titleInput"
                :fieldLevel="fieldsLevels.title"
                :editMode="editMode"
                v-model="document.title"
                errorMessage="Заголовок не может быть пустой"
                :state="() => state('title')"
        ></s-input-group>

        <s-input-group
                title="Комментарий"
                id="commentInput"
                :fieldLevel="fieldsLevels.comment"
                :editMode="editMode"
                v-model="document.comment"
                errorMessage="Комментарий не может быть пустой"
                :state="() => state('comment')"
        ></s-input-group>

        <b-row>
            <b-col>
                <s-select-group
                        title="Вид документа"
                        id="documentKindInput"
                        :fieldLevel="fieldsLevels.documentKind"
                        :editMode="editMode"
                        v-model="document.documentKind"
                        :valueTitle="document.documentKind"
                        errorMessage="Вид документа должен быть заполнен"
                        :state="() => state('documentKind')"
                        :selectConfig="documentKindConfig"
                ></s-select-group>
            </b-col>

            <b-col>
                <s-select-group
                    title="Адресат"
                    id="addresseeInput"
                    :fieldLevel="fieldsLevels.addressee"
                    :editMode="editMode"
                    v-model="document.addressee"
                    :valueTitle="document.addresseeTitle"
                    errorMessage="Адресат должен быть заполнен"
                    :state="() => state('addressee')"
                    :selectConfig="userSelectConfig"
                    ></s-select-group>
            </b-col>
        </b-row>

        <s-multi-select-group
                title="Адресаты (копии)"
                id="addresseeCopiesInput"
                :fieldLevel="fieldsLevels.addresseeCopies"
                :editMode="editMode"
                v-model="document.addresseeCopies"
                :valueTitle="document.addresseeCopiesTitles"
                :selectConfig="addresseeCopiesSelectConfig"
                :state="() => state('addresseeCopies')"
                errorMessage="Адресаты должены быть заполнены"
        ></s-multi-select-group>

        <div class="s-document-block-delimiter"></div>


        <s-select-group
                title="Корреспондент"
                id="externalOrganizationInput"
                :fieldLevel="fieldsLevels.externalOrganization"
                :editMode="editMode"
                v-model="document.externalOrganization"
                :valueTitle="document.externalOrganizationTitle"
                errorMessage="Корреспондент должен быть заполнен"
                :state="() => state('externalOrganization')"
                :selectConfig="externalOrganizationConfig"
        ></s-select-group>

        <b-row>
            <b-col>
                <s-select-group
                        title="Кем подписано"
                        id="externalSignerInput"
                        :fieldLevel="fieldsLevels.externalSigner"
                        :editMode="editMode"
                        v-model="document.externalSigner"
                        :valueTitle="document.externalSigner"
                        :depends="document"
                        errorMessage="Кем подписано должно быть заполнено"
                        :state="() => state('externalSigner')"
                        :selectConfig="externalOrganizationPersonConfig"
                ></s-select-group>
            </b-col>
            <b-col>
                <s-select-group
                        title="Исполнитель"
                        id="externalExecutorInput"
                        :fieldLevel="fieldsLevels.externalExecutor"
                        :editMode="editMode"
                        v-model="document.externalExecutor"
                        :valueTitle="document.externalExecutor"
                        :depends="document"
                        errorMessage="Исполнитель должен быть заполнен"
                        :state="() => state('externalExecutor')"
                        :selectConfig="externalOrganizationPersonConfig"
                ></s-select-group>
            </b-col>
        </b-row>

        <b-row>
            <b-col>
                <s-input-group
                        title="Исходящий номер"
                        id="externalNumberInput"
                        :fieldLevel="fieldsLevels.externalNumber"
                        :editMode="editMode"
                        v-model="document.externalNumber"
                        errorMessage="Исходящий номер не может быть пустой"
                        :state="() => state('externalNumber')"
                ></s-input-group>

            </b-col>
            <b-col>
                <s-date-group
                        title="Исходящая дата"
                        id="externalDateInput"
                        :fieldLevel="fieldsLevels.externalDate"
                        :editMode="editMode"
                        v-model="document.externalDate"
                        :valueTitle="document.externalDateStr"
                        :depends="document"
                        errorMessage="Исходящая дата должена быть заполнена"
                        :state="() => state('externalDate')"
                ></s-date-group>

            </b-col>
        </b-row>

        <div class="s-document-block-delimiter"></div>

        <b-row>
            <b-col>
                <s-input-group
                        title="Номер документа"
                        id="documentNumberInput"
                        :fieldLevel="fieldsLevels.documentNumber"
                        :editMode="editMode"
                        v-model="document.documentNumber"
                        errorMessage="Номер документа не может быть пустой"
                        :state="() => state('documentNumber')"
                ></s-input-group>

            </b-col>
            <b-col>
                <s-input-group
                        title="Дело"
                        id="caseNumberInput"
                        :fieldLevel="fieldsLevels.caseNumber"
                        :editMode="editMode"
                        v-model="document.caseNumber"
                        errorMessage="Дело не может быть пустым"
                        :state="() => state('caseNumber')"
                ></s-input-group>

            </b-col>
        </b-row>

        <b-row>
            <b-col>
                <s-input-group
                        title="Количество листов"
                        id="pageCountInput"
                        :fieldLevel="fieldsLevels.pageCount"
                        :editMode="editMode"
                        v-model="document.pageCount"
                        errorMessage="Количество листов должно быть целым числом"
                        :state="() => state('pageCount')"
                ></s-input-group>

            </b-col>
            <b-col>
                <s-input-group
                        title="Количество приложений"
                        id="appendixCountInput"
                        :fieldLevel="fieldsLevels.appendixCount"
                        :editMode="editMode"
                        v-model="document.appendixCount"
                        errorMessage="Количество приложений должно быть целым числом"
                        :state="() => state('appendixCount')"
                ></s-input-group>

            </b-col>
        </b-row>

        <div class="s-document-block-delimiter"></div>

        <b-row>
            <b-col>
                <s-date-group
                        title="Дата регистрации"
                        id="registrationDateInput"
                        :fieldLevel="fieldsLevels.registrationDate"
                        :editMode="editMode"
                        v-model="document.registrationDate"
                        :valueTitle="document.registrationDateStr"
                        :depends="document"
                        errorMessage="Дата регистрации должена быть заполнена"
                        :state="() => state('registrationDate')"
                ></s-date-group>

            </b-col>
            <b-col>
                <s-select-group
                        title="Регистратор"
                        id="registrarInput"
                        :fieldLevel="fieldsLevels.registrar"
                        :editMode="editMode"
                        v-model="document.registrar"
                        :valueTitle="document.registrarTitle"
                        errorMessage="Регистратор должен быть заполнен"
                        :state="() => state('registrar')"
                        :selectConfig="userSelectConfig"
                ></s-select-group>
            </b-col>
        </b-row>

        <b-row>
            <b-col>
                <s-date-group
                        title="Дата исполнения"
                        id="executionDateInput"
                        :fieldLevel="fieldsLevels.executionDate"
                        :editMode="editMode"
                        v-model="document.executionDate"
                        :valueTitle="document.executionDateStr"
                        :depends="document"
                        errorMessage="Дата исполнения должена быть заполнена"
                        :state="() => state('executionDate')"
                ></s-date-group>
                
            </b-col>
            <b-col>
                <s-select-group
                        title="Исполнитель"
                        id="executorInput"
                        :fieldLevel="fieldsLevels.executor"
                        :editMode="editMode"
                        v-model="document.executor"
                        :valueTitle="document.executorTitle"
                        errorMessage="Исполнитель должен быть заполнен"
                        :state="() => state('executor')"
                        :selectConfig="userSelectConfig"
                ></s-select-group>
            </b-col>
        </b-row>

        <b-row>
            <b-col>
                <s-select-group
                        title="Контроллер"
                        id="controllerInput"
                        :fieldLevel="fieldsLevels.controller"
                        :editMode="editMode"
                        v-model="document.controller"
                        :valueTitle="document.controllerTitle"
                        errorMessage="Контроллер должен быть заполнен"
                        :state="() => state('controller')"
                        :selectConfig="userSelectConfig"
                ></s-select-group>
            </b-col>
            <b-col>
                <s-select-group
                        title="Способ доставки"
                        id="deliveryMethodInput"
                        :fieldLevel="fieldsLevels.deliveryMethod"
                        :editMode="editMode"
                        v-model="document.deliveryMethod"
                        :valueTitle="document.deliveryMethod"
                        errorMessage="Способ доставки должен быть заполнен"
                        :state="() => state('deliveryMethod')"
                        :selectConfig="deliveryMethodConfig"
                ></s-select-group>
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
    import SMultiSelectGroup from "../components/fields/MultiSelectGroup";
    import SMultiSelect from "../components/fields/MultiSelect";
    import SInputGroup from "../components/fields/InputGroup"
    import SDateGroup from "../components/fields/DateGroup"
    import axios from 'axios';

    import datePicker from 'vue-bootstrap-datetimepicker';

    export default {
        components: {SSelect, SMultiSelect, datePicker,
            SSelectGroup, SInputGroup, SMultiSelectGroup,
            SDateGroup},

        mounted() {

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
            }
        },
        methods: {
            getFormState() {
                let result = [];
                for (let field in this.document) {
                    let state = this.state(field);
                    if (state !== null) {
                        result.push(this.fieldTitles[field]);
                    }
                }

                return result;
            },
            state(field) {
                if (field === 'title') {
                    return this.document.title.length > 0 ? null : false;
                }

                if (field === 'comment') {
                    return this.fieldsLevels.comment < 3 || this.document.comment && this.document.comment.length > 0 ? null : false;
                }

                if (field === 'documentKind') {
                    return (!this.document.documentKind && this.fieldsLevels.documentKind == 3) ? false : null;
                }

                if (field === 'addressee') {
                    return (!this.document.addressee && this.fieldsLevels.addressee == 3) ? false : null;
                }

                if (field === 'addresseeCopies') {
                    return (!this.document.addresseeCopies && this.fieldsLevels.addresseeCopies == 3) ? false : null;
                }

                if (field === 'externalOrganization') {
                    return (!this.document.externalOrganization && this.fieldsLevels.externalOrganization == 3) ? false : null;
                }

                if (field === 'externalSigner') {
                    return (!this.document.externalSigner && this.fieldsLevels.externalSigner == 3) ? false : null;
                }

                if (field === 'externalExecutor') {
                    return (!this.document.externalSigner && this.fieldsLevels.externalSigner == 3) ? false : null;
                }

                if (field === 'externalNumber') {
                    return (!this.document.externalNumber && this.fieldsLevels.externalNumber == 3) ? false : null;
                }

                if (field === 'externalDate') {
                    return (!this.document.externalDate && this.fieldsLevels.externalDate == 3) ? false : null;
                }

                if (field === 'documentNumber') {
                    return (!this.document.documentNumber && this.fieldsLevels.documentNumber == 3) ? false : null;
                }

                if (field === 'caseNumber') {
                    return (!this.document.caseNumber && this.fieldsLevels.caseNumber == 3) ? false : null;
                }

                if (field === 'pageCount') {
                    if (this.document.pageCount) {
                        if (!(this.isNormalInteger(this.document.pageCount) && this.document.pageCount >= 0)) {
                            return false;
                        }
                    } else if (this.fieldsLevels.pageCount == 3) {
                        return false;
                    }
                }
                if (field === 'appendixCount') {
                    if (this.document.appendixCount) {
                        if (!(this.isNormalInteger(this.document.appendixCount) && this.document.appendixCount >= 0)) {
                            return false;
                        }
                    } else if (this.fieldsLevels.appendixCount == 3) {
                        return false;
                    }
                }

                if (field === 'registrationDate') {
                    return (!this.document.registrationDate && this.fieldsLevels.registrationDate == 3) ? false : null;
                }

                if (field === 'registrar') {
                    return (!this.document.registrar && this.fieldsLevels.registrar == 3) ? false : null;
                }

                if (field === 'executionDate') {
                    return (!this.document.executionDate && this.fieldsLevels.executionDate == 3) ? false : null;
                }

                if (field === 'executor') {
                    return (!this.document.executor && this.fieldsLevels.executor == 3) ? false : null;
                }

                if (field === 'controller') {
                    return (!this.document.controller && this.fieldsLevels.controller == 3) ? false : null;
                }

                if (field === 'deliveryMethod') {
                    return (!this.document.deliveryMethod && this.fieldsLevels.deliveryMethod == 3) ? false : null;
                }

                return null;
            },
            isNormalInteger(str) {
                return /^\+?(0|[1-9]\d*)$/.test(str);
            }
        },
        data() {
            const documentData = this.document;
            return {
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