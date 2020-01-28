<template>
    <s-input-group
            :title="title"
            :id="name + 'Input'"
            :fieldLevel="dataSource.fieldsLevels[name] || dataSource.fieldsLevels['*']"
            :editMode="dataSource.editMode"
            v-model="dataSource.document[name]"
            :errorMessage="title + ' не может быть пустой'"
            :state="() => dataSource.state(name)"
    ></s-input-group>
</template>

<style>

</style>

<script>
    import SInputGroup from "./InputGroup"

    export default {
        components: {SInputGroup},
        name: "s-input-field",
        props: {
            name: {},
            title: {},
        },
        mounted() {
            this.dataSource.fieldTitles[this.name] = this.title;
        },
        computed: {
            dataSource() {return this.$parent.dataSource;}
        },
        methods: {

        }
    }
</script>