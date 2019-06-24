<template>
    <b-form class="s-document-form">
        <b-form-group
                label="Заголовок"
                label-for="titleInput"
                v-if="fieldsLevels.title > 0">
            <b-form-input
                    id="titleInput"
                    :state="state('title')"
                    v-model="document.title"
                    required :readonly="!editMode || fieldsLevels.title == 1" :plaintext="!editMode || fieldsLevels.title == 1"  />
            <b-form-invalid-feedback>
                Заголовок не может быть пустой
            </b-form-invalid-feedback>
        </b-form-group>

        <b-form-group
                label="Комментарий"
                label-for="commentInput"
                v-if="fieldsLevels.comment > 0">
            <b-form-input
                    id="commentInput"
                    :state="state('comment')"
                    v-model="document.comment"
                    required :readonly="!editMode || fieldsLevels.comment == 1" :plaintext="!editMode || fieldsLevels.comment == 1"  />
            <b-form-invalid-feedback>
                Комментарий не может быть пустой
            </b-form-invalid-feedback>
        </b-form-group>

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
                        :state="() => {return (!this.document.documentKind && this.fieldsLevels.documentKind == 3) ? false : null;}"
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
                    :state="() => {return (!this.document.addressee && this.fieldsLevels.addressee == 3) ? false : null;}"
                    :selectConfig="userSelectConfig"
                    ></s-select-group>
            </b-col>
        </b-row>

        <b-form-group
                label="Адресаты (копии)"
                label-for="addresseeCopiesInput"
                v-if="fieldsLevels.addresseeCopies > 0">
            <b-form-input
                    v-if="!editMode || fieldsLevels.addresseeCopies == 1"
                    id="addresseeCopiesInput"
                    v-model="document.addresseeCopiesTitles.join()"
                    required :readonly="!editMode || fieldsLevels.addresseeCopies == 1" :plaintext="!editMode || fieldsLevels.addresseeCopies == 1"  />
            <s-multi-select v-if="editMode && fieldsLevels.addresseeCopies > 1" id="addresseeCopiesInput" :config="addresseeCopiesSelectConfig"
                      @value="(val) => document.addresseeCopies=val" :value="document.addresseeCopies"
                      :valueTitle="document.addresseeCopiesTitles"></s-multi-select>
        </b-form-group>

        <div class="s-document-block-delimiter"></div>

        <div class="s-document-block-delimiter"></div>

        <b-row>
            <b-col>
                <b-form-group
                        label="Номер документа"
                        label-for="documentNumberInput"
                        v-if="fieldsLevels.documentNumber > 0">
                    <b-form-input
                            id="documentNumberInput"
                            :state="state('documentNumber')"
                            v-model="document.documentNumber"
                            required :readonly="!editMode || fieldsLevels.documentNumber == 1" :plaintext="!editMode || fieldsLevels.documentNumber == 1"  />
                </b-form-group>
            </b-col>
            <b-col>
                <b-form-group
                        label="Дело"
                        label-for="caseNumberInput"
                        v-if="fieldsLevels.caseNumber > 0">
                    <b-form-input
                            id="caseNumberInput"
                            :state="state('caseNumber')"
                            v-model="document.caseNumber"
                            required :readonly="!editMode || fieldsLevels.caseNumber == 1" :plaintext="!editMode || fieldsLevels.caseNumber == 1"  />
                </b-form-group>
            </b-col>
        </b-row>

        <b-row>
            <b-col>
                <b-form-group
                        label="Количество листов"
                        label-for="pageCountInput"
                        v-if="fieldsLevels.comment > 0">
                    <b-form-input
                            id="pageCountInput"
                            :state="state('pageCount')"
                            v-model="document.pageCount"
                            required :readonly="!editMode || fieldsLevels.pageCount == 1" :plaintext="!editMode || fieldsLevels.pageCount == 1"  />
                    <b-form-invalid-feedback>
                        Количество листов должно быть целым числом
                    </b-form-invalid-feedback>
                </b-form-group>
            </b-col>
            <b-col>
                <b-form-group
                        label="Количество приложений"
                        label-for="appendixCountInput"
                        v-if="fieldsLevels.appendixCount > 0">
                    <b-form-input
                            id="rappendixCountInput"
                            :state="state('appendixCount')"
                            v-model="document.appendixCount"
                            required :readonly="!editMode || fieldsLevels.appendixCount == 1" :plaintext="!editMode || fieldsLevels.appendixCount == 1"  />
                    <b-form-invalid-feedback>
                        Количество приложений должно быть целым числом
                    </b-form-invalid-feedback>
                </b-form-group>
            </b-col>
        </b-row>

        <div class="s-document-block-delimiter"></div>

        <b-row>
            <b-col>
                <b-form-group
                        label="Дата регистрации"
                        label-for="registrationDateInput"
                        v-if="fieldsLevels.registrationDate > 0">
                    <b-form-input
                            v-if="!editMode || fieldsLevels.registrationDate == 1"
                            id="registrationDateInput"
                            v-model="document.registrationDateStr"
                            required :readonly="!editMode || fieldsLevels.registrationDate == 1" :plaintext="!editMode || fieldsLevels.registrationDate == 1"  />
                    <date-picker
                            v-if="editMode && fieldsLevels.registrationDate > 1"
                            id="registrationDateInput"
                            :state="state('registrationDate')"
                            v-model="document.registrationDate"
                            :config="dateConfig"/>
                </b-form-group>
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
                        :state="() => {return (!this.document.registrar && this.fieldsLevels.registrar == 3) ? false : null;}"
                        :selectConfig="userSelectConfig"
                ></s-select-group>
            </b-col>
        </b-row>

        <b-row>
            <b-col>
                <b-form-group
                        label="Дата исполнения"
                        label-for="executionDateInput"
                        v-if="fieldsLevels.executionDate > 0">
                    <b-form-input
                            v-if="!editMode || fieldsLevels.executionDate == 1"
                            id="executionDateInput"
                            v-model="document.executionDateStr"
                            required :readonly="!editMode || fieldsLevels.executionDate == 1" :plaintext="!editMode || fieldsLevels.executionDate == 1"  />
                    <date-picker
                            v-if="editMode && fieldsLevels.executionDate > 1"
                            id="executionDateInput"
                            :state="state('executionDate')"
                            v-model="document.executionDate"
                            :config="dateConfig"/>
                </b-form-group>
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
                        :state="() => {return (!this.document.executor && this.fieldsLevels.executor == 3) ? false : null;}"
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
                        :state="() => {return (!this.document.controller && this.fieldsLevels.controller == 3) ? false : null;}"
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
                        :state="() => {return (!this.document.deliveryMethod && this.fieldsLevels.deliveryMethod == 3) ? false : null;}"
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
    import SMultiSelect from "../components/fields/MultiSelect";
    import axios from 'axios';

    import datePicker from 'vue-bootstrap-datetimepicker';

    export default {
        components: {SSelect, SMultiSelect, datePicker, SSelectGroup},

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
                    {title: 'Тест'},
                    {title: 'Требование'},
                    {title: 'Уведомление'},
                    {title: 'Указ'},
                    {title: 'Указание Минтранса'}],
            };
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
                return !(this.titleState ===  false);
            },
            state(field) {
                if (field === 'title') {
                    return this.document.title.length > 0 ? null : false;
                }

                if (field === 'comment') {
                    return this.fieldsLevels.comment < 3 || this.document.comment && this.document.comment.length > 0 ? null : false;
                }

                if (field === 'documentKind') {
                    return this.document.documentKind ? null : false;
                }

                if (field === 'addressee') {
                    return (!this.document.addressee && this.fieldsLevels.addressee == 3) ? false : null;
                }

                if (field === 'externalOrganization') {
                    return (!this.document.externalOrganization && this.fieldsLevels.externalOrganization == 3) ? false : null;
                }

                if (field === 'pageCount' && this.document.pageCount) {
                    if (!(this.isNormalInteger(this.document.pageCount) && this.document.pageCount >= 0)) {
                        return false;
                    }
                }
                if (field === 'appendixCount' && this.document.appendixCount) {
                    if (!(this.isNormalInteger(this.document.appendixCount) && this.document.appendixCount >= 0)) {
                        return false;
                    }
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
                testSelectConfig: null,
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