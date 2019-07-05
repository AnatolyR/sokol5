<template>
    <div>
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
</template>

<script>
    import SSelect from "../components/fields/Select";
    import SInputGroup from "../components/fields/InputGroup";

    export default {
        components: {SSelect, SInputGroup},
        name: 's-contragent-form',
        mounted() {

        },
        data() {
            return {
                fieldTitles: {
                    fullName: "Полное название",
                    address: "Адрес",
                    phone: "Телефон",
                    title: "Заголовок"
                },
                highlightErrors: false
            }
        },
        computed: {
            contragent: {
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
                this.highlightErrors = true;
                let result = [];
                (["title", "fullName", "address", "phone"]).forEach(field => {
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
                        fullName: null,
                        address: null,
                        phone: null
                    }
                }
            },
            editMode: {},
            fieldsLevels: {
                default: function() {
                    return {
                        id: "1",
                        title: "3",
                        fullName: "3",
                        address: "3",
                        phone: "2"
                    }
                }
            }
        }
    }
</script>

<style>
    
    
</style>