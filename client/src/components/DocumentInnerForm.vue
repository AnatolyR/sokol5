<template>
    <b-form class="s-document-form">
        <s-input-field name="title" title="Заголовок" />

        <s-input-field name="comment" title="Комментарий" />

        <b-row>
            <b-col>
                <s-select-field name="documentKind" title="Вид документа" config="documentKindConfig"/>
            </b-col>

            <b-col>
                <s-select-field name="executor" title="Исполнитель" config="userSelectConfig"/>
            </b-col>
        </b-row>

        <s-multi-select-field name="approvers" title="Согласование" config="addresseeCopiesSelectConfig"/>

        <s-multi-select-field name="signers" title="Подписание" config="addresseeCopiesSelectConfig"/>

        <s-multi-select-field name="addresseeCopies" title="Получатели" config="addresseeCopiesSelectConfig"/>

        <div class="s-document-block-delimiter"></div>

        <b-row>
            <b-col>
                <s-input-field name="documentNumber" title="Рег. документа" />
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
                <s-select-field name="registrar" title="Регистратор" config="userSelectConfig"/>
            </b-col>
        </b-row>

        <b-row>
            <b-col>
                <s-input-field name="forDocumentNumber" title="На №" />
            </b-col>
            <b-col>
                <s-date-field name="forRegistrationDate" title="На дату" />
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
    import DocumentFormBase from "./DocumentFormBase";
    import axios from 'axios';

    import datePicker from 'vue-bootstrap-datetimepicker';

    export default {
        extends: DocumentFormBase,
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
                        fieldTitles: this.fieldTitles,
                        state: this.state.bind(this),
                        dictionariesConfigs: this.dictionariesConfigs
                    }
                }
            }
        },
        methods: {

        },
        data() {
            const documentData = this.document;
            return {
                showState: false,
                fieldTitles: {

                }
            }
        }
    }
</script>