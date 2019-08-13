<template>
    <div class="s-select-fix">
         <select ref="select"></select>
    </div>
</template>

<style>
    .selectize-dropdown, .selectize-input, .selectize-input input {
        line-height: unset !important;
    }
</style>

<script>
    import $ from 'jquery';
    if (!$().selectize) {
        require('selectize')
    }

    export default {
        name: "s-select",
        mounted() {
            if (this.created) {
                return;
            }

            const config = {...this.config};
            if (!config.options && this.value) {
                config.options = [{id: this.value, title: this.valueTitle ? this.valueTitle : this.value}];
            }

            var $select = $(this.$refs["select"]).selectize(config);
            var selectize = $select[0].selectize;
            setTimeout(() => selectize.setValue(this.value), 10);
            selectize.depends = this.depends;

            selectize.on("change", (val) => {
                if (val !== this.value) {
                    if (this.emitWithTitle) {
                        this.$emit('value', {val: val, title: selectize.getItem(val).text()});
                    } else {
                        this.$emit('value', val);
                    }
                }
            });

            this.created = true;
        },
        data() {
            return {
                created: false
            }
        },
        props: [
            'config',
            'value',
            'valueTitle',
            'depends',
            'emitWithTitle'
        ]
    }
</script>

