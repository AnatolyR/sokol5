<template>
    <div class="s-user-container">
        <div v-if="errorMessage" class="">
            <b-alert show variant="danger">{{errorMessage}}</b-alert>
        </div>
        <div v-if="successMessage" class="">
            <b-alert show variant="success">{{successMessage}}</b-alert>
        </div>
        <div v-if="loading" class="">
            <b-spinner variant="secondary" label="Загрузка..." />
        </div>
        <div v-if="!loading && user" class="">
            <!-- BUTTONS -->
            <div class="s-user-button-bar">
                <b-button v-if="openedFromFolder" size="sm" variant="light" @click="back"><font-awesome-icon icon="angle-left" /> Назад</b-button>
                <b-button v-if="buttons['edit'] && !editMode" @click="edit" size="sm">Редактировать</b-button>
                <b-button v-if="editMode" variant="success" @click="save" size="sm">Сохранить</b-button>
                <b-button v-if="editMode" variant="danger" @click="cancel" size="sm">Отменить</b-button>

<!--                <b-button v-if="buttons['reset_pass']" @click="resetPass" variant="warning" size="sm">Сбросить пароль</b-button>-->
            </div>


            <!-- HEADER -->
            <h5 class="s-user-header">Сотрудник</h5>
            <h4 class="s-user-subheader">{{user.title}}</h4>
            <!--<span class="s-user-status">Статус: {{user.status}}</span>-->


            <!-- TABS -->
            <ul class="s-user-tabs nav nav-tabs" id="myTab" role="tablist">
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
                <li class="nav-item" v-if="buttons['edit']">
                    <a class="nav-link"
                       v-bind:class="{ active: tab === 'roles' }"
                       data-toggle="tab"
                       href="#"
                       @click.prevent="showRoles">
                        Роли
                    </a>
                </li>

            </ul>

            <s-user-form v-if="tab === 'attributes'" class="s-user-form"  ref="attributesForm"
                         :edit-mode="editMode" v-model="user"
                         :fieldsLevels="fieldsLevels">

            </s-user-form>

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

            <div v-if="tab === 'roles'">
                <s-roles-form :user-id="userId" :buttons="{add: buttons['role_add'], del: buttons['role_del']}"></s-roles-form>
            </div>

        </div>
    </div>
</template>

<style>
    .s-user-form {
        margin: 1em;
    }

    .s-user-button-bar {
        margin: 0.5em 0 1em 0;
    }
    .s-user-container {
        margin: 0.5em;
    }

    .s-user-button-bar button {
        margin-right: 0.5em !important;
    }

    .s-user-tabs {
        margin-top: 0.5em;
    }

    h4.s-user-subheader {
        margin-bottom: 0.25em;
    }

    h5.s-user-header {
        margin-bottom: 0.25em;
    }

    .s-user-form label {
        margin-bottom: 0;
        font-weight: bold;
    }
    .s-user-form input {

    }

    .s-user-form {
        margin: 1em;
    }

</style>

<script>
    import axios from "axios";
    import SSelect from "../components/fields/Select";
    import SInputGroup from "../components/fields/InputGroup";
    import SRolesForm from "../components/RolesForm";

    import SUserForm from "../components/UserForm";

    export default {
        // components: {SSelect, SDocumentForm},
        components: {SSelect, SInputGroup, SUserForm, SRolesForm},
        mounted() {
            this.loadUser();
            this.loadActions();
        },
        props: [
            "userId"
        ],
        computed: {
            openedFromFolder() {
                return this.$route.meta.openedFromDictionary === true;
            }
        },
        watch: {
            userId () {
                this.loadUser();
                this.loadActions();
            }
        },
        methods: {
            loadActions() {
                axios.get(`/api/user/availableActions`)
                    .then((res) => {
                        res.data.forEach((a) => this.buttons[a] = true);
                    });
            },
            goBack() {
                this.$router.go(-1);
            },
            edit() {
                this.editMode = true;
            },
            cancel() {
                this.errorMessage = null;
                this.user = JSON.parse(this.userBackup);
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

                const user = JSON.parse(JSON.stringify(this.user));

                axios.post(`/api/users`, user).then(() => {
                    this.$bvToast.toast(`Пользователь сохранен`, {
                        title: 'Успешно',
                        variant: 'success',
                        solid: true,
                        autoHideDelay: 2000
                    });
                    this.loadUser();
                }).catch((err) => {
                    console.log('err', err);
                    this.errorMessage = 'Не удается сохранить пользователя';
                });
            },
            loadUser() {
                this.editMode = false;
                this.errorMessage = null;
                this.loading = true;
                axios.get(`/api/users/${this.userId}`).then((response) => {
                    const user = response.data;
                    user._links = null;
                    // const fieldsLevels = response.data.fields;
                    this.fieldsLevels = {
                        id: "1",
                        title: "3",
                        firstName: "3",
                        lastName: "3",
                        middleName: "2",
                        username: "2",
                        email: "2"
                    };
                    // console.log("levels ", fieldsLevels);


                    this.user = user;
                    this.userBackup = JSON.stringify(user);

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
            showRoles() {
                this.tab = 'roles';
            }
        },
        data() {
            return {
                loading: true,
                errorMessage: null,
                successMessage: null,
                user: null,
                fieldsLevels: null,

                tab: 'attributes',
                editMode: false,

                attachesCount: 0,

                formState: null,
                uncorrectFields: "",
                buttons: {
                    edit: false,
                    add: false,
                    del: false,
                    reset_pass: false,
                    roles: false,
                    role_add: false,
                    role_del: false
                }
            }
        }
    }
</script>