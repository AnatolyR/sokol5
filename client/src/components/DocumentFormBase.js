export default {
    methods: {
        getFormState() {
            let result = [];
            this.showState = true;
            for (let field in this.document) {
                let state = this.state(field);
                if (state !== null) {
                    result.push(this.fieldTitles[field] || field);
                }
            }

            return result;
        },
        state(field) {
            if (!this.showState) {
                return null;
            }

            if (field === 'title') {
                return this.document.title && this.document.title.length > 0 ? null : false;
            }

            if (field === 'pageCount' || field === 'appendixCount') {
                if (this.document[field]) {
                    if (!(this.isNormalInteger(this.document[field]) && this.document[field] >= 0)) {
                        return false;
                    }
                } else if (this.fieldsLevels[field] === '3') {
                    return false;
                }
                return null;
            }

            if (this.fieldsLevels[field]) {
                return (this.fieldsLevels[field] === '3' && !this.document[field]) ? false : null;
            } else {
                return (this.fieldsLevels['*'] === '3' && !this.document[field]) ? false : null;
            }
        },
        isNormalInteger(str) {
            return /^\+?(0|[1-9]\d*)$/.test(str);
        }
    }
};