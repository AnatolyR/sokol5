<template>
    <s-multi-select-group
            :title="title"
            :id="name + 'Input'"
            :fieldLevel="dataSource.fieldsLevels[name] || dataSource.fieldsLevels['*']"
            :editMode="dataSource.editMode"
            v-model="dataSource.document[name]"
            :valueTitle="dataSource.document[name + 'Title'] || dataSource.document[name + 'Titles'] || dataSource.document[name]"
            :errorMessage="title + ' не может быть пустой'"
            :state="() => dataSource.state(name)"
            :selectConfig="dataSource[name + 'Config']"
    ></s-multi-select-group>
</template>

<style>

</style>

<script>
    import SMultiSelectGroup from "./MultiSelectGroup"

    export default {
        components: {SMultiSelectGroup},
        name: "s-multi-select-field",
        props: {
            name: {},
            title: {},
        },
        computed: {
            dataSource() {return this.$parent.dataSource;}
        },
        methods: {

        }
    }
</script>