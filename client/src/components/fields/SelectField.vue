<template>
    <s-select-group
            :title="title"
            :id="name + 'Input'"
            :fieldLevel="dataSource.fieldsLevels[name] || dataSource.fieldsLevels['*']"
            :editMode="dataSource.editMode"
            v-model="dataSource.document[name]"
            :valueTitle="dataSource.document[name + 'Title'] || dataSource.document[name]"
            :errorMessage="title + ' не может быть пустой'"
            :depends="dataSource.document"
            :state="() => dataSource.state(name)"
            :selectConfig="dataSource[name + 'Config']"
    ></s-select-group>
</template>

<style>

</style>

<script>
    import SSelectGroup from "./SelectGroup"

    export default {
        components: {SSelectGroup},
        name: "s-select-field",
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