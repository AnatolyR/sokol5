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

        <b-form-group
                label="Комментарий"
                label-for="commentInput">
            <b-form-input
                    id="commentInput"
                    :state="state('comment')"
                    v-model="document.comment"
                    required :readonly="!editMode" :plaintext="!editMode"  />
        </b-form-group>

        <b-row>
            <b-col>
                <b-form-group label="Вид документа" label-for="exampleInput2">
                    <b-form-input
                            v-if="!editMode"
                            id="documentKindInput"
                            v-model="document.documentKind"
                            required :readonly="!editMode" :plaintext="!editMode"  />
                    <s-select v-if="editMode" id="documentKindInput" :config="testSelectConfig" @value="(val) => document.documentKind=val" :value="document.documentKind"></s-select>
                </b-form-group>
            </b-col>

            <b-col>
                <b-form-group
                        label="Адресат"
                        label-for="addresseeInput">
                    <b-form-input
                            id="addresseeInput"
                            :state="state('addressee')"
                            v-model="document.addresseeTitle"
                            required :readonly="!editMode" :plaintext="!editMode"  />
                </b-form-group>
            </b-col>
        </b-row>

        <b-form-group
                label="Адресаты (копии)"
                label-for="addresseeCopiesInput">
            <b-form-input
                    id="addresseeCopiesInput"
                    :state="state('addresseeCopies')"
                    v-model="document.addresseeCopiesTitles"
                    required :readonly="!editMode" :plaintext="!editMode"  />
        </b-form-group>

        <div class="s-document-block-delimiter"></div>

        <b-form-group
                label="Корреспондент"
                label-for="externalOrganizationInput">
            <b-form-input
                    id="externalOrganizationInput"
                    :state="state('externalOrganization')"
                    v-model="document.externalOrganizationTitle"
                    required :readonly="!editMode" :plaintext="!editMode"  />
        </b-form-group>

        <b-row>
            <b-col>
                <b-form-group
                        label="Кем подписано"
                        label-for="externalSignerInput">
                    <b-form-input
                            id="externalSignerInput"
                            :state="state('externalSigner')"
                            v-model="document.externalSigner"
                            required :readonly="!editMode" :plaintext="!editMode"  />
                </b-form-group>
            </b-col>
            <b-col>
                <b-form-group
                        label="Исполнитель"
                        label-for="externalExecutorInput">
                    <b-form-input
                            id="eexternalExecutorInput"
                            :state="state('externalExecutor')"
                            v-model="document.externalExecutor"
                            required :readonly="!editMode" :plaintext="!editMode"  />
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
                            id="externalDateInput"
                            :state="state('externalDate')"
                            v-model="document.externalDate"
                            required :readonly="!editMode" :plaintext="!editMode"  />
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
                            id="registrationDateInput"
                            :state="state('registrationDate')"
                            v-model="document.registrationDate"
                            required :readonly="!editMode" :plaintext="!editMode"  />
                </b-form-group>
            </b-col>
            <b-col>
                <b-form-group
                        label="Регистратор"
                        label-for="registrarInput">
                    <b-form-input
                            id="registrarInput"
                            :state="state('registrar')"
                            v-model="document.registrar"
                            required :readonly="!editMode" :plaintext="!editMode"  />
                </b-form-group>
            </b-col>
        </b-row>

        <b-row>
            <b-col>
                <b-form-group
                        label="Дата исполнения"
                        label-for="executionDateInput">
                    <b-form-input
                            id="executionDateInput"
                            :state="state('executionDate')"
                            v-model="document.executionDate"
                            required :readonly="!editMode" :plaintext="!editMode"  />
                </b-form-group>
            </b-col>
            <b-col>
                <b-form-group
                        label="Исполнитель"
                        label-for="executorInput">
                    <b-form-input
                            id="executorInput"
                            :state="state('executor')"
                            v-model="document.executor"
                            required :readonly="!editMode" :plaintext="!editMode"  />
                </b-form-group>
            </b-col>
        </b-row>

        <b-row>
            <b-col>
                <b-form-group
                        label="Контроллер"
                        label-for="controllerInput">
                    <b-form-input
                            id="controllerInput"
                            :state="state('controller')"
                            v-model="document.controller"
                            required :readonly="!editMode" :plaintext="!editMode"  />
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
            },
            state(field) {
                return null;
            }
        },
        data() {
            return {
                testSelectConfig: null
            }
        }
    }
</script>