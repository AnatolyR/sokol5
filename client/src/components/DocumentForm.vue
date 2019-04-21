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
        </b-form-group>

        <b-row>
            <b-col>
                <b-form-group label="Вид документа" label-for="documentKindInput"
                              v-if="fieldsLevels.documentKind > 0">
                    <b-form-input
                            v-if="!editMode || fieldsLevels.documentKind == 1"
                            id="documentKindInput"
                            v-model="document.documentKind"
                            required :readonly="!editMode || fieldsLevels.documentKind == 1" :plaintext="!editMode || fieldsLevels.documentKind == 1"  />
                    <!--<s-select v-if="editMode && fieldsLevels.documentKind > 1" id="documentKindInput"-->
                    <s-select v-if="editMode && fieldsLevels.documentKind > 1" id="documentKindInput"
                              :config="testSelectConfig" @value="(val) => document.documentKind=val"
                              :value="document.documentKind"
                              v-bind:class="{'s-invalid-field': state('documentKind') === false ? true : false}"></s-select>
                    <div v-if="editMode && fieldsLevels.documentKind > 1" class="invalid-feedback" v-bind:style="{display: state('documentKind') === false ? 'block' : 'none'}">
                        Вид документа должен быть заполнен
                    </div>
                </b-form-group>
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

        <b-form-group
                label="Корреспондент"
                label-for="externalOrganizationInput"
                v-if="fieldsLevels.externalOrganization > 0">
            <b-form-input
                    v-if="!editMode || fieldsLevels.externalOrganization == 1"
                    id="externalOrganizationInput"
                    v-model="document.externalOrganizationTitle"
                    required :readonly="!editMode || fieldsLevels.externalOrganization == 1" :plaintext="!editMode || fieldsLevels.externalOrganization == 1"  />
            <s-select v-if="editMode && fieldsLevels.externalOrganization > 1" id="externalOrganizationInput" :config="externalOrganizationConfig"
                      @value="(val) => document.externalOrganization=val" :value="document.externalOrganization"
                      :valueTitle="document.externalOrganizationTitle"
                      v-bind:class="{'s-invalid-field': state('externalOrganization') === false ? true : false}"></s-select>
            <div v-if="editMode && fieldsLevels.externalOrganization > 1" class="invalid-feedback" v-bind:style="{display: state('externalOrganization') === false ? 'block' : 'none'}">
                Корреспондент должен быть заполнен
            </div>
        </b-form-group>

        <b-row>
            <b-col>
                <b-form-group
                        label="Кем подписано"
                        label-for="externalSignerInput">
                    <b-form-input
                            v-if="!editMode"
                            id="externalSignerInput"
                            v-model="document.externalSigner"
                            required :readonly="!editMode" :plaintext="!editMode"  />
                    <s-select v-if="editMode" id="externalSignerInput" :config="externalOrganizationPersonConfig"
                              @value="(val) => document.externalSigner=val" :value="document.externalSigner"
                              :depends="document"
                              :valueTitle="document.externalSigner"></s-select>
                </b-form-group>
            </b-col>
            <b-col>
                <b-form-group
                        label="Исполнитель"
                        label-for="externalExecutorInput">
                    <b-form-input
                            v-if="!editMode"
                            id="externalExecutorInput"
                            v-model="document.externalExecutor"
                            required :readonly="!editMode" :plaintext="!editMode"  />
                    <s-select v-if="editMode" id="externalExecutorInput" :config="externalOrganizationPersonConfig"
                              @value="(val) => document.externalExecutor=val" :value="document.externalExecutor"
                              :depends="document"
                              :valueTitle="document.externalExecutor"></s-select>
                </b-form-group>
            </b-col>
        </b-row>

        <b-row>
            <b-col>
                <b-form-group
                        label="Исходящий номер"
                        label-for="externalNumberInput">
                    <b-form-input
                            id="externalNumberInput"
                            :state="state('externalNumber')"
                            v-model="document.externalNumber"
                            required :readonly="!editMode" :plaintext="!editMode"  />
                </b-form-group>
            </b-col>
            <b-col>
                <b-form-group
                        label="Исходящая дата"
                        label-for="externalDateInput">
                    <b-form-input
                            v-if="!editMode"
                            id="externalDateInput"
                            v-model="document.externalDateStr"
                            required :readonly="!editMode" :plaintext="!editMode"  />
                    <date-picker
                            v-if="editMode"
                            id="externalDateInput"
                            :state="state('externalDate')"
                            v-model="document.externalDate"
                            :config="dateConfig"/>
                </b-form-group>
            </b-col>
        </b-row>

        <div class="s-document-block-delimiter"></div>

        <b-row>
            <b-col>
                <b-form-group
                        label="Номер документа"
                        label-for="documentNumberInput">
                    <b-form-input
                            id="documentNumberInput"
                            :state="state('documentNumber')"
                            v-model="document.documentNumber"
                            required :readonly="!editMode" :plaintext="!editMode"  />
                </b-form-group>
            </b-col>
            <b-col>
                <b-form-group
                        label="Дело"
                        label-for="caseNumberInput">
                    <b-form-input
                            id="caseNumberInput"
                            :state="state('caseNumber')"
                            v-model="document.caseNumber"
                            required :readonly="!editMode" :plaintext="!editMode"  />
                </b-form-group>
            </b-col>
        </b-row>

        <b-row>
            <b-col>
                <b-form-group
                        label="Количество листов"
                        label-for="pageCountInput">
                    <b-form-input
                            id="pageCountInput"
                            :state="state('pageCount')"
                            v-model="document.pageCount"
                            required :readonly="!editMode" :plaintext="!editMode"  />
                    <b-form-invalid-feedback>
                        Количество листов должно быть целым числом
                    </b-form-invalid-feedback>
                </b-form-group>
            </b-col>
            <b-col>
                <b-form-group
                        label="Количество приложений"
                        label-for="appendixCountInput">
                    <b-form-input
                            id="rappendixCountInput"
                            :state="state('appendixCount')"
                            v-model="document.appendixCount"
                            required :readonly="!editMode" :plaintext="!editMode"  />
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
                        label-for="registrationDateInput">
                    <b-form-input
                            v-if="!editMode"
                            id="registrationDateInput"
                            v-model="document.registrationDateStr"
                            required :readonly="!editMode" :plaintext="!editMode"  />
                    <date-picker
                            v-if="editMode"
                            id="registrationDateInput"
                            :state="state('registrationDate')"
                            v-model="document.registrationDate"
                            :config="dateConfig"/>
                </b-form-group>
            </b-col>
            <b-col>
                <b-form-group
                        label="Регистратор"
                        label-for="registrarInput">
                    <b-form-input
                            v-if="!editMode"
                            id="registrarInput"
                            v-model="document.registrarTitle"
                            required :readonly="!editMode" :plaintext="!editMode"  />
                    <s-select v-if="editMode" id="registrarInput" :config="userSelectConfig"
                              @value="(val) => document.registrar=val" :value="document.registrar"
                              :valueTitle="document.registrarTitle"></s-select>
                </b-form-group>
            </b-col>
        </b-row>

        <b-row>
            <b-col>
                <b-form-group
                        label="Дата исполнения"
                        label-for="executionDateInput">
                    <b-form-input
                            v-if="!editMode"
                            id="executionDateInput"
                            v-model="document.executionDateStr"
                            required :readonly="!editMode" :plaintext="!editMode"  />
                    <date-picker
                            v-if="editMode"
                            id="executionDateInput"
                            :state="state('executionDate')"
                            v-model="document.executionDate"
                            :config="dateConfig"/>
                </b-form-group>
            </b-col>
            <b-col>
                <b-form-group
                        label="Исполнитель"
                        label-for="executorInput">
                    <b-form-input
                            v-if="!editMode"
                            id="executorInput"
                            v-model="document.executorTitle"
                            required :readonly="!editMode" :plaintext="!editMode"  />
                    <s-select v-if="editMode" id="executorInput" :config="userSelectConfig"
                              @value="(val) => document.executor=val" :value="document.executor"
                              :valueTitle="document.executorTitle"></s-select>
                </b-form-group>
            </b-col>
        </b-row>

        <b-row>
            <b-col>
                <b-form-group
                        label="Контроллер"
                        label-for="controllerInput">
                    <b-form-input
                            v-if="!editMode"
                            id="controllerInput"
                            v-model="document.controllerTitle"
                            required :readonly="!editMode" :plaintext="!editMode"  />
                    <s-select v-if="editMode" id="controllerInput" :config="userSelectConfig"
                              @value="(val) => document.controller=val" :value="document.controller"
                              :valueTitle="document.controllerTitle"></s-select>
                </b-form-group>
            </b-col>
            <b-col>
                <b-form-group
                        label="Способ доставки"
                        label-for="deliveryMethodInput">
                    <b-form-input
                            id="edeliveryMethodInput"
                            :state="state('deliveryMethod')"
                            v-model="document.deliveryMethod"
                            required :readonly="!editMode" :plaintext="!editMode"  />
                </b-form-group>
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
    import SSelect from "../components/Select";
    import SSelectGroup from "../components/SelectGroup";
    import SMultiSelect from "../components/MultiSelect";
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
                }

            }
        }
    }
</script>