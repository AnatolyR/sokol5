<template>
    <div>
        <s-input-group
                title="Заголовок"
                id="titleInput"
                :fieldLevel="fieldsLevels.title"
                :editMode="editMode"
                v-model="user.title"
                errorMessage="Заголовок не может быть пустой"
                :state="() => state('title')"
        ></s-input-group>

        <s-input-group
                title="Имя"
                id="firstNameInput"
                :fieldLevel="fieldsLevels.firstName"
                :editMode="editMode"
                v-model="user.firstName"
                errorMessage="Имя не может быть пустым"
                :state="() => state('firstName')"
        ></s-input-group>

        <s-input-group
                title="Фамилия"
                id="lastNameInput"
                :fieldLevel="fieldsLevels.lastName"
                :editMode="editMode"
                v-model="user.lastName"
                errorMessage="Фамилия не может быть пустой"
                :state="() => state('lastName')"
        ></s-input-group>

        <s-input-group
                title="Отчество"
                id="middleNameInput"
                :fieldLevel="fieldsLevels.middleName"
                :editMode="editMode"
                v-model="user.middleName"
                errorMessage="Отчество не может быть пустым"
                :state="() => state('middleName')"
        ></s-input-group>
    </div>
</template>

<script>
    import SSelect from "../components/fields/Select";
    import SInputGroup from "../components/fields/InputGroup";

    export default {
        components: {SSelect, SInputGroup},
        name: 's-user-form',
        mounted() {

        },
        data() {
            return {
                fieldTitles: {
                    firstName: "Имя",
                    lastName: "Фамилия",
                    middleName: "Отчество",
                    title: "Заголовок"
                },
                highlightErrors: false
            }
        },
        computed: {
            user: {
                get() {
                    return this.value;
                },
                set(v) {
                    this.$emit('input', v)
                }
            }
        },
        methods: {
            state(field) {
                if (!this.highlightErrors) {
                    return null;
                }
                let fieldValue = this.user[field];
                let level = this.fieldsLevels[field];
                if (level === "3") {
                    if (fieldValue === undefined || fieldValue === null || fieldValue.length === 0) {
                        return false;
                    }
                }
                return null;
            },
            getFormState() {
                this.highlightErrors = true;
                let result = [];
                (["title", "firstName", "lastName", "middleName"]).forEach(field => {
                    let state = this.state(field);
                    if (state !== null) {
                        result.push(this.fieldTitles[field]);
                    }
                });

                return result;
            }
        },
        props: {
            value: {
                default: function() {
                    return {
                        title: null,
                        firstName: null,
                        lastName: null,
                        middleName: null
                    }
                }
            },
            editMode: {},
            fieldsLevels: {
                default: function() {
                    return {
                        id: "1",
                        title: "3",
                        firstName: "3",
                        lastName: "3",
                        middleName: "2"
                    }
                }
            }
        }
    }
</script>

<style>
    
    
</style>