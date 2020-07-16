<template>
    <div>
        <div>
            <div class="s-folder-buttons-bar">
                <b-button v-if="buttons.pages !== false && pagesCount > 1" :disabled="!isPrevPage" size="sm" @click="prevPage"><font-awesome-icon icon="angle-left" /> Предыдущая</b-button>

                <b-dropdown v-if="buttons.pages !== false && pagesCount > 1" id="dropdown-1" :text="currentPageItem" size="sm">
                    <b-dropdown-item v-for="item in pageItems" :key="item.title"
                                     :active="activePage(item.page)"
                                     boundary="viewport"
                                     @click="setPage(item.page)">{{item.title}}</b-dropdown-item>
                </b-dropdown>

                <b-button v-if="buttons.pages !== false && pagesCount > 1" :disabled="!isNextPage" size="sm" @click="nextPage">Следующая <font-awesome-icon icon="angle-right" /></b-button>

                <b-dropdown v-if="buttons.columns !== false" id="dropdown-2" text="Колонки" size="sm">
                    <div class="dropdown-item" v-for="col in columns"><b-form-checkbox style="width: 100%;" v-model="col.visible" boundary="viewport">{{col.title}}</b-form-checkbox></div>
                </b-dropdown>

                <b-button v-if="buttons.refresh !== false" size="sm" @click="update">Обновить</b-button>
                <b-button v-if="buttons.filter" :variant="this.displayFilter ? 'outline-secondary' : (conditions && conditions.length > 0 ? 'warning' : '')" size="sm" @click="toggleFilter">Фильтр</b-button>
                <b-button v-if="buttons.add" variant="success" size="sm" @click="showAddModal">Добавить</b-button>
                <b-button v-if="buttons.del" :disabled="toDeleteItems === ''" variant="danger" size="sm" @click="showDelModal">Удалить</b-button>

                <b-button v-if="buttons.edit && !editMode" @click="edit" size="sm">Редактировать</b-button>
                <b-button v-if="buttons.edit && editMode" variant="success" @click="save" size="sm">Сохранить</b-button>
                <b-button v-if="buttons.edit && editMode" variant="danger" @click="cancel" size="sm">Отменить</b-button>

                <b-modal :size="addType === 'task' ? 'lg' : ''" id="add-modal" ref="add-modal" title="Добавление" ok="'Сохранить'" cancel="'Отмена'">
                    <p class="my-4">

                        <s-dictionary-value v-if="addType === 'string'" v-model="toAddValue" ref="addForm"></s-dictionary-value>

                        <s-dictionary-value v-if="addType === 'role'" v-model="toAddValue" ref="addForm"></s-dictionary-value>

                        <s-user-form v-if="addType === 'user'" v-model="toAddValue" :edit-mode="true" ref="addForm"></s-user-form>

                        <s-contragent-form v-if="addType === 'contragent'" v-model="toAddValue" :edit-mode="true" ref="addForm"></s-contragent-form>

                        <s-add-attach v-if="addType === 'attach'" v-model="toAddValue" ref="addForm"></s-add-attach>

                        <s-add-link v-if="addType === 'link'" v-model="toAddValue" ref="addForm"></s-add-link>

                        <s-execution-form v-if="addType === 'task'" add-tasks="true" v-model="toAddValue" ref="addForm"></s-execution-form>
                    </p>
                    <template slot="modal-footer" slot-scope="{ ok, cancel, hide }">
                        <b-form-checkbox v-model="addOneMore"
                        >
                            {{addMoreTitle}}
                        </b-form-checkbox>
                        <b-button size="sm" variant="light" @click="cancel()">
                            Отмена
                        </b-button>
                        <b-button size="sm" variant="success" @click="addValue()">
                            Сохранить
                        </b-button>
                    </template>
                </b-modal>
                <b-toast id="add-success-toast" title="Добавление" static no-auto-hide>
                    Значение успешно добавлено
                </b-toast>

                <b-modal id="info-modal" ref="info-modal" modal-class="s-modal-incorrect-fields" title="Некорректно заполненные поля">
                    <p class="my-4">
                        {{uncorrectFields}}
                    </p>
                    <template slot="modal-footer" slot-scope="{ ok, cancel, hide }">
                        <b-button size="sm" variant="info" @click="cancel()">
                            Ок
                        </b-button>
                    </template>
                </b-modal>

                <b-modal id="del-modal" ref="del-modal" title="Подтверждение удаления" modal-class="s-modal-confirm-delete">
                    <p class="my-4">{{deleteTitle}}:
                    {{toDeleteItems}}
                    </p>
                    <template slot="modal-footer" slot-scope="{ ok, cancel, hide }">
                        <b-button size="sm" variant="light" @click="cancel()">
                            Отмена
                        </b-button>
                        <b-button size="sm" variant="danger" @click="deleteSelected()">
                            Удалить
                        </b-button>
                    </template>
                </b-modal>
            </div>
        </div>
        <keep-alive>
            <s-filter v-if="displayFilter" :propertySelect="propertySelect()" @update="update" @value="(val) => this.conditions = val"></s-filter>
        </keep-alive>
        <table class="table table-bordered table-sm">
            <thead>
            <tr>
                <th v-for="col in visibleColumns" scope="col" @click="doSort(col.id)" :style="col.type === 'checkbox' ? 'width: 2em;' : ''">
                    <span v-if="col.type !== 'checkbox'">
                        {{col.title}}
                        <font-awesome-icon v-if="sortProperty === col.id && sortDirection" :icon="sortDirectionAngle" />
                    </span>
                </th>
            </tr>
            </thead>
            <tbody>

            <!-- ========================== -->
            <template v-for="item in data">
            <tr :key="item.id">
                <td v-for="col in visibleColumns" :style="{width: col.width ? col.width : ''}"
                    @click="() => {if (col.type === 'checkbox') item.selected = !item.selected}">
                    <span v-if="col.type !== 'link'
                        && col.type !== 'checkbox'
                        && col.type !== 'userSelect'
                        && col.type !== 'dateSelect'
                        && col.type !== 'taskLink'
                        && col.type !== 'date'
                        && col.type !== 'datetime'
                        && col.type !== 'detailsRow'
                        && (col.type !== 'fileLink' || !buttons['view'])"
                          :class="`s-table-cell-${col.id}`">{{getItemValue(item, col.id)}}</span>

                    <span v-if="col.type === 'date'"
                          :class="`s-table-cell-${col.id}`">{{getDateValue(item, col.id)}}</span>

                    <span v-if="col.type === 'datetime'"
                          :class="`s-table-cell-${col.id}`">{{getDateTimeValue(item, col.id)}}</span>

                    <router-link v-if="col.type === 'link'"
                                 :class="`s-table-cell-${col.id}`"
                                 :to="col.path + (col.idField ? getItemValue(item, col.idField) : item.id)">{{ getItemValue(item, col.id) }}</router-link>

                    <router-link v-if="col.type === 'taskLink'"
                                 :class="`s-table-cell-${col.id}`"
                                 :to="'/document/' + getItemValue(item, 'document.id') + '/task/' + item.id">{{ getItemValue(item, col.id) }}</router-link>

                    <router-link v-if="col.type === 'fileLink' && buttons['view']"
                                 :class="`s-table-cell-${col.id}`"
                                 target="_blank"
                                 :to="'/api/file/' + item.id">{{ getItemValue(item, col.id) }}</router-link>

                    <b-form-checkbox v-if="col.type === 'checkbox' && (item.id || col.ref)"
                                     :class="`s-table-cell-${col.id}`"
                                     v-model="item.selected" @click.native="(e) => e.preventDefault()"></b-form-checkbox>

                    <b-button size="sm" variant="light" v-if="col.type === 'detailsRow' && (infoType !== 'report' || item.comment)"
                                     :class="`s-table-cell-${col.id}`"
                              :pressed.sync="item.infoSelected" @click.native="(e) => e.preventDefault()">{{item.infoSelected ? 'Скрыть' : 'Показать'}}</b-button>

                    <span v-if="col.type === 'userSelect' && !editMode"
                          :class="`s-table-cell-${col.id}`">{{item[col.id + 'Title']}}</span>
                    <s-select v-if="col.type === 'userSelect' && editMode"
                              :config="userSelectConfig"
                              :emit-with-title="true"
                              @value="(val) => selectUser(val, item, col.id)"
                              :value="getItemValue(item, col.id)"
                              :valueTitle="item[col.id + 'Title']"
                    ></s-select>

                    <date-picker style="position:relative"
                            v-if="col.type === 'dateSelect'"
                            v-model="item.executionDate"
                            :id="'datapicker-' + item.id"
                            :config="dateConfig"/>
                </td>
            </tr>
            <tr :key="item.id + '_details'" v-if="item.infoSelected">
                <td colspan="10" v-if="item.infoSelected && infoType === 'history'">
                    <table>
                        <tr>
                            <th>Поле</th>
                            <th>Старое значение</th>
                            <th>Новое значение</th>
                        </tr>
                        <tr v-for="f in getItemInfo(item)">
                            <td>{{f.title}}</td>
                            <td>{{f.prev}}</td>
                            <td>{{f.current}}</td>
                        </tr>
                    </table>
                </td>
                <td colspan="10" v-if="item.infoSelected && infoType === 'report'">
                        <div>
<!--                            <div>{{item.statusTitle}}</div>-->
<!--                            <div>{{item.executedDate}}</div>-->
                            <div>{{item.comment}}</div>
                        </div>
                </td>
            </tr>
            </template>
            <!-- ========================== -->
            
            </tbody>
        </table>
        <div>
            <div class="s-folder-buttons-bar">
                <b-button v-if="buttons.pages !== false && pagesCount > 1" :disabled="!isPrevPage" size="sm" @click="prevPage"><font-awesome-icon icon="angle-left" /> Предыдущая</b-button>


                <b-dropdown v-if="buttons.pages !== false && pagesCount > 1" id="dropdown-3" :text="currentPageItem" size="sm">
                    <b-dropdown-item v-for="item in pageItems" :key="item.title"
                                     :active="activePage(item.page)"
                                     boundary="viewport"
                                     @click="setPage(item.page)">{{item.title}}</b-dropdown-item>
                </b-dropdown>

                <b-button v-if="buttons.pages !== false && pagesCount > 1" :disabled="!isNextPage" size="sm" @click="nextPage">Следующая <font-awesome-icon icon="angle-right" /></b-button>

            </div>
        </div>
    </div>
</template>

<style>
    .s-folder-buttons-bar {
        margin-bottom: 0.5em;
    }
    .s-folder-buttons-bar button {
        margin-right: 0.5em;
    }
    .s-folder-buttons-bar .dropdown {
        /*margin-right: 0.5em;*/
    }
    .s-folder-buttons-bar .btn-group {

    }
    .s-folder-buttons-bar .dropdown-menu {
        min-width: unset;
        max-height: 400px;
        overflow: scroll;
    }
</style>

<script>
    import SFilter from "./Filter";
    import axios from 'axios';
    import SDictionaryValue from './DictionaryValue';
    import SUserForm from "../components/UserForm";
    import SContragentForm from "../components/ContragentForm";
    import SAddAttach from "./AddAttachForm";
    import SAddLink from "./AddLinkForm";
    import SSelect from "./fields/Select";
    import datePicker from 'vue-bootstrap-datetimepicker';
    import uuid from '../uuid.js';
    import moment from 'moment';

    export default {
        name: 's-table',
        components: {
            SAddAttach, SFilter, SDictionaryValue, SUserForm,
            SContragentForm, SSelect, datePicker, SAddLink,
            SExecutionForm: () => import("../components/ExecutionForm")
        },
        mounted() {
            this.editMode = this.editProp;
            this.update();
        },
        computed: {
            visibleColumns() {
                return this.columns.filter((c) => c.visible);
            },
            items() {
                return this.data;
            },
            pagesCount() {
                return this.totalPages;
            },
            isNextPage() {
                return this.page < this.totalPages - 1;
            },
            isPrevPage() {
                return this.page > 0;
            },
            pageItems() {
                let selectors = [];
                for (let p = 1; p <= this.totalPages; p++) {
                    selectors.push({
                        page: p - 1,
                        title: p + " / " + this.totalPages
                    });
                }
                return selectors;
            },
            currentPageItem() {
                return (this.page + 1) + " / " + (this.totalPages ? this.totalPages : '') ;
            },
            sortDirectionAngle() {
                if (this.sortDirection === "DESC") {
                    return "angle-down";
                } else if (this.sortDirection === "ASC") {
                    return "angle-up";
                } else {
                    return null;
                }
            },
            toDeleteItems() {
                return this.deleteCallback ? (this.data.filter((i) => i.selected === true).length === 0 ? '' : null) : this.data.filter((i) => i.selected === true).map((i) => i[this.deleteInfoTitleField]).join(", ");
            }
        },
        props: {
            loadData: {
                type: Function
            },
            columns: {},
            editProp: {
                default: false
            },
            deleteTitle: {
                default: "Будут удалены значения"
            },
            addMoreTitle: {
                default: "Добавить еще одно значение"
            },
            buttons: {
                default: function () {
                    return {}
                }
            },
            deleteUrl: {},
            addUrl: {},
            infoType: {},
            addType: {
                default: 'default'
            },
            objectId: {},
            objectType: {},
            deleteCallback: {
                type: Function
            },
            addCallback: {
                type: Function
            },
            sortDirection: null,
            sortProperty: null,
            deleteInfoTitleField: {
                default: 'title'
            }
        },
        methods: {
            update() {
                this.loadData({size: this.size, page: this.page,
                        sortDirection: this.sortDirection, sortProperty: this.sortProperty,
                        conditions: this.conditions}).then((res) => {
                    let fullData = res.data;

                    // if (this.buttons.del) {
                        fullData.content.forEach(i => i.selected = false);
                        fullData.content.forEach(i => i.infoSelected = false);
                    // }

                    this.data = fullData.content;
                    this.totalPages = fullData.totalPages;
                });
            },
            nextPage() {
                if (this.isNextPage) {
                    this.page = this.page + 1;
                }
            },
            prevPage() {
                if (this.isPrevPage) {
                    this.page = this.page - 1;
                }
            },
            setPage(page) {
                this.page = page;
            },
            activePage(page) {
                return page === this.page;
            },
            doSort(sortProperty) {
                if (this.sortProperty != null && this.sortProperty !== sortProperty) {
                    this.sortDirection = null;
                }

                this.sortProperty = sortProperty;
                if (this.sortDirection == null) {
                    this.sortDirection = "DESC";
                } else if (this.sortDirection === "DESC") {
                    this.sortDirection = "ASC";
                } else if (this.sortDirection === "ASC") {
                    this.sortDirection = null;
                    this.sortProperty = null;
                }
                this.update();
            },
            propertySelect() {
                return {
                    maxItems: 1,
                    valueField: 'id',
                    labelField: 'title',
                    preload: true,
                    options: this.columns,
                };
            },
            toggleFilter() {
                this.displayFilter = !this.displayFilter;
            },
            showAddModal() {
                if (this.addCallback) {
                    this.addCallback();
                    return;
                }
                
                this.$refs['add-modal'].show();
            },
            hideAddModal() {
                this.$refs['add-modal'].hide();
            },
            toggleAddModal() {
                this.$refs['add-modal'].toggle('#toggle-btn');
            },
            showDelModal() {
                if (this.deleteCallback) {
                    this.deleteCallback(this.data.filter((i) => i.selected === true));
                    return;
                }

                this.$refs['del-modal'].show();
            },
            deleteSelected() {
                this.$refs['del-modal'].hide();

                if (this.deleteCallback) {
                    this.deleteCallback(this.data.filter((i) => i.selected === true));
                    return;
                }
                
                let ids = this.data.filter((i) => i.selected === true).map((i) => i.id);
                axios.delete(`/api/${this.deleteUrl}/${ids.join(',')}`).then((res) => {

                    this.$bvToast.toast(`Значение(я) успешно удалены`, {
                        variant: 'success',
                        solid: true,
                        autoHideDelay: 2000
                    });

                    this.update();
                }).catch((res) => {
                    this.$bvToast.toast(`Не удалось удалить значения`, {
                        variant: 'danger',
                        solid: true,
                        autoHideDelay: 4000
                    });
                });
            },
            addValue() {
                if (this.addCallback) {
                    this.addCallback();
                    return;
                }

                let fields = this.$refs.addForm && this.$refs.addForm.getFormState();
                if (fields && fields.length > 0) {
                    this.uncorrectFields = fields.join(", ");
                    this.$refs['info-modal'].show();
                    return;
                }

                let errorHandler = (error) => {
                    this.loading = false;
                    if (error.response && error.response.status === 403) {
                        this.$bvToast.toast(`Недостаточно прав для добавления значения`, {
                            variant: 'danger',
                            solid: true
                        });
                    } else {
                        this.$bvToast.toast(`Не удается добавить значение`, {
                            variant: 'danger',
                            solid: true
                        });
                    }
                };

                if (this.addType === 'attach') {
                    let formData = new FormData();
                    formData.append('file', this.toAddValue.file);
                    formData.append('objectId', this.objectId);
                    formData.append('objectType', this.objectType);

                    axios.post('/api/file',
                        formData,
                        {
                            headers: {
                                'Content-Type': 'multipart/form-data'
                            }
                        }
                    ).then((res) => {
                        this.$bvToast.toast(`Значение успешно добавлено`, {
                            variant: 'success',
                            solid: true,
                            autoHideDelay: 2000
                        });

                        this.update();
                    })
                        .catch(errorHandler);
                } else if (this.addType === 'task') {
                    let executionData = this.$refs.addForm.getData();
                    executionData.documentId = this.objectId;
                    executionData.actionId = "addtasks";
                    this.$refs.addForm.resetExecutor();
                    axios.post(`/api/document/${this.objectId}/actions`, executionData).then(() => {
                        this.$bvToast.toast(`Статус обновлен`, {
                            variant: 'success',
                            solid: true,
                            autoHideDelay: 2000
                        });
                        this.update();
                    }).catch(errorHandler);
                } else {
                    if (this.addType === 'role') {
                        this.toAddValue['userId'] = this.objectId;
                    } else if (this.addType === 'link') {
                        this.toAddValue['fromDocument'] = this.objectId;
                    }

                    axios.post(`/api/${this.addUrl}`, this.toAddValue).then((res) => {
                        this.$bvToast.toast(`Значение успешно добавлено`, {
                            variant: 'success',
                            solid: true,
                            autoHideDelay: 2000
                        });

                        this.update();
                    }).catch(errorHandler);
                }

                if (!this.addOneMore) {
                    this.$refs['add-modal'].hide();
                }
                if (this.$refs.addForm) {
                    this.$refs.addForm.highlightErrors = false;
                }
                this.toAddValue = {};
            },
            selectUser(val, item, colId) {
                item[colId] = val.val;
                item[colId + "Title"] = val.title;
            },
            getData() {
                return this.data;
            },
            getItemValue(item, id) {
                if (id.indexOf(".") > 0) {
                    let ids = id.split(".");
                    return item[ids[0]][ids[1]];
                } else {
                    return item[id];
                }
            },
            getItemInfo(item) {
                let changes = JSON.parse(this.getItemValue(item, "data"));
                let fields = [];
                let prev  = changes["prev"];
                let current  = changes["current"];
                changes["fields"].forEach(f => {
                    fields.push({
                        title: f.title,
                        prev: prev[f.id] ? prev[f.id] : "",
                        current: current[f.id] ? current[f.id] : ""
                    });
                });
                return fields;
            },
            getDateValue(item, id) {
                let val = this.getItemValue(item, id);
                return val ? new Date(val).toLocaleDateString("ru") : '';
            },
            getDateTimeValue(item, id) {
                let val = this.getItemValue(item, id);
                return val ? moment(new Date(val)).format("DD.MM.YYYY HH:mm") : '';
            },
            edit() {
                this.editMode = true;
            },
            cancel() {
                
                this.editMode = false;
            },
            save() {
                this.editMode = false;
            }
        },
        watch: {
            page() {
                this.update();
            }
        },
        data() {
            return {
                size: 20,
                page: 0,
                totalPages: null,
                totalElements: null,
                data: [],
                editMode: false,
                conditions: null,
                displayFilter: false,
                toAddValue: {},
                addOneMore: false,
                uncorrectFields: null,
                userSelectConfig: {
                    maxItems: 1,
                    //plugins: ['remove_button'],
                    valueField: 'id',
                    labelField: 'title',
                    searchField: 'title',
                    preload: true,
                    load(query, callback) {
                        if (!query.length) {
                            return callback();
                        }
                        axios.get(`/api/users/search/userByTitle?title=%25${query}%25`).then((res) => {
                            const users = res.data._embedded.users;
                            callback(users);
                        }).catch(() => {
                            callback();
                        })
                    }
                },
                dateConfig: {
                    locale:'ru'
                }
            }
        }
    }
</script>