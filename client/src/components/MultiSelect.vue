<template>
    <div>
         <select ref="select"></select>
    </div>
</template>

<script>
    import $ from 'jquery';
    if (!$().selectize) {
        require('selectize')
    }

    export default {
        name: "s-multi-select",
        mounted() {
            if (this.created) {
                return;
            }

            const config = {...this.config};
            if (this.value) {
                config.options = [];
                for (let i = 0; i < this.value.length; i++) {
                    config.options.push({id: this.value[i], title: this.valueTitle[i]});
                }
            }

            var $select = $(this.$refs["select"]).selectize(config);
            var selectize = $select[0].selectize;
            setTimeout(() => selectize.setValue(this.value), 10);

            selectize.on("change", (val) => {
                if (val !== this.value) {
                    this.$emit('value', val);
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
            'valueTitle'
        ]
    }
</script>

