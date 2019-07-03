<template>
    <div class="s-contragent-container">
        <div v-if="errorMessage" class="">
            <b-alert show variant="danger">{{errorMessage}}</b-alert>
        </div>
        <div v-if="successMessage" class="">
            <b-alert show variant="success">{{successMessage}}</b-alert>
        </div>
        <div v-if="loading" class="">
            <b-spinner variant="secondary" label="Загрузка..." />
        </div>
        <div v-if="!loading && contragent" class="">
            <!-- BUTTONS -->
            <div class="s-contragent-button-bar">
                <b-button v-if="openedFromFolder" size="sm" variant="light" @click="back"><font-awesome-icon icon="angle-left" /> Назад</b-button>
                <b-button v-if="!editMode" @click="edit" size="sm">Редактировать</b-button>
                <b-button v-if="editMode" variant="success" @click="save" size="sm">Сохранить</b-button>
                <b-button v-if="editMode" variant="danger" @click="cancel" size="sm">Отменить</b-button>
            </div>


            <!-- HEADER -->
            <h5 class="s-contragent-header">Контрагент</h5>
            <h4 class="s-contragent-subheader">{{contragent.title}}</h4>
            <!--<span class="s-contragent-status">Статус: {{user.status}}</span>-->


            <!-- TABS -->
            <ul class="s-contragent-tabs nav nav-tabs" id="myTab" role="tablist">
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

            </ul>

            <div v-if="tab === 'attributes'" class="s-contragent-form">
                <s-input-group
                        title="Заголовок"
                        id="titleInput"
                        :fieldLevel="fieldsLevels.title"
                        :editMode="editMode"
                        v-model="contragent.title"
                        errorMessage="Заголовок не может быть пустой"
                        :state="() => state('title')"
                ></s-input-group>

                <s-input-group
                        title="Полное название"
                        id="fullNameInput"
                        :fieldLevel="fieldsLevels.fullName"
                        :editMode="editMode"
                        v-model="contragent.fullName"
                        errorMessage="Полное название не может быть пустым"
                        :state="() => state('fullName')"
                ></s-input-group>

                <s-input-group
                        title="Адрес"
                        id="addressInput"
                        :fieldLevel="fieldsLevels.address"
                        :editMode="editMode"
                        v-model="contragent.address"
                        errorMessage="Адрес не может быть пустой"
                        :state="() => state('address')"
                ></s-input-group>

                <s-input-group
                        title="Телефон"
                        id="phoneInput"
                        :fieldLevel="fieldsLevels.phone"
                        :editMode="editMode"
                        v-model="contragent.phone"
                        errorMessage="Телефон не может быть пустым"
                        :state="() => state('phone')"
                ></s-input-group>
            </div>

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
                Вложения
            </div>

        </div>
    </div>
</template>

<style>
    .s-contragent-form {
        margin: 1em;
    }

    .s-contragent-button-bar {
        margin: 0.5em 0 1em 0;
    }
    .s-contragent-container {
        margin: 0.5em;
    }

    .s-contragent-button-bar button {
        margin-right: 0.5em !important;
    }

    .s-contragent-tabs {
        margin-top: 0.5em;
    }

    h4.s-contragent-subheader {
        margin-bottom: 0.25em;
    }

    h5.s-contragent-header {
        margin-bottom: 0.25em;
    }

    .s-contragent-form label {
        margin-bottom: 0;
        font-weight: bold;
    }
    .s-contragent-form input {

    }

    .s-contragent-form {
        margin: 1em;
    }

</style>

<script>
    import axios from "axios";
    import SSelect from "../components/fields/Select";
    import SInputGroup from "../components/fields/InputGroup";

    export default {
        // components: {SSelect, SDocumentForm},
        components: {SSelect, SInputGroup},
        mounted() {
            this.loadContragent();
        },
        props: [
            "contragentId"
        ],
        computed: {
            openedFromFolder() {
                return this.$route.meta.openedFromDictionary === true;
            }
        },
        watch: {
            contragentId () {
                this.loadContragent();
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
                this.contragent = JSON.parse(this.contragentBackup);
                this.editMode = false;
            },
            back() {
                if (this.editMode) {
                    this.cancel();
                }
                this.$router.go(-1);
            },
            state(field) {
                let fieldValue = this.contragent[field];
                let level = this.fieldsLevels[field];
                if (level === "3") {
                    if (fieldValue === undefined || fieldValue === null || fieldValue.length === 0) {
                        return false;
                    }
                }
                return null;
            },
            getFormState() {
                let result = [];
                for (let field in this.contragent) {
                    let state = this.state(field);
                    if (state !== null) {
                        result.push(this.fieldTitles[field]);
                    }
                }

                return result;
            },
            save() {
                this.errorMessage = null;
                let fields = this.getFormState();
                if (fields && fields.length > 0) {
                    this.uncorrectFields = fields.join(", ");
                    this.$refs['info-modal'].show();
                    // this.errorMessage = "Некорректно заполненные поля";
                    return;
                } else {
                    this.errorMessage = null;
                }

                const contragent = JSON.parse(JSON.stringify(this.contragent));

                axios.post(`/api/contragents`, contragent).then(() => {
                    this.$bvToast.toast(`Контрагент сохранен`, {
                        title: 'Успешно',
                        variant: 'success',
                        solid: true,
                        autoHideDelay: 2000
                    });
                    this.loadContragent();
                }).catch((err) => {
                    console.log('err', err);
                    this.errorMessage = 'Не удается сохранить контрагента';
                });
            },
            loadContragent() {
                this.editMode = false;
                this.errorMessage = null;
                this.loading = true;
                axios.get(`/api/contragents/${this.contragentId}`).then((response) => {
                    const contragent = response.data;
                    contragent._links = null;
                    // const fieldsLevels = response.data.fields;
                    this.fieldsLevels = {
                        id: "1",
                        title: "3",
                        fullName: "3",
                        address: "3",
                        phone: "2"
                    };
                    // console.log("levels ", fieldsLevels);


                    this.contragent = contragent;
                    this.contragentBackup = JSON.stringify(contragent);

                    console.log("contragent", this.contragent);

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
            }
        },
        data() {
            return {
                fieldTitles: {
                    fullName: "Полное название",
                    address: "Адрес",
                    phone: "Телефон",
                    title: "Заголовок"
                },
                loading: true,
                errorMessage: null,
                successMessage: null,
                contragent: null,
                fieldsLevels: null,

                tab: 'attributes',
                editMode: false,

                attachesCount: 1,

                formState: null,
                uncorrectFields: ""
            }
        }
    }
</script>