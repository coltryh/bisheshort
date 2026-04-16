/*eslint-disable*/
<template>
  <div
    v-loading="formLoding || filterLoding || saveLoading"
    class="dialog-content"
  >
    <div
      v-if="showAlert"
      class="alert-class"
    >
      <el-alert
        v-if="showAlert"
        @close="closeAlert"
        :title="alertmsg"
        type="warning"
        show-icon
      ></el-alert>
    </div>
    <div
      v-if="showAlert1"
      class="alert-class"
    >
      <el-alert
        v-if="showAlert1"
        :title="alertmsg1"
        type="warning"
        show-icon
      ></el-alert>
    </div>
    <div
      v-if="showAlert2"
      class="alert-class"
    >
      <el-alert
        v-if="showAlert2"
        :title="alertmsg2"
        type="warning"
        show-icon
      ></el-alert>
    </div>
    <div
      v-if="showAlert3"
      class="alert-class"
    >
      <el-alert
        v-if="showAlert3"
        :title="alertmsg3"
        type="warning"
        show-icon
      ></el-alert>
    </div>
    <el-form
      ref="editForm"
      v-loading="tagLoading"
      label-position="top"
      label-width="50px"
      size="small"
      :model="editForm"
      :rules="formRules"
    >
      <el-collapse v-model="activeNames">
        <el-collapse-item title="基础信息" name="basicInfo">
          <el-row>
            <el-col :span="6">
              <el-form-item label="医院名称" prop="hospitalName">
                {{ terminalName || '&nbsp;' }}
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="医院编码 SE Code" prop="hospitalCode">
                {{ terminalCode || '&nbsp;' }}
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="省份" prop="hospitalProvince">
                {{ terminalProvince || '&nbsp;' }}
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="医院级别" prop="hospitalLevel">
                {{ terminalLevel || '&nbsp;' }}
              </el-form-item>
            </el-col>
          </el-row>
        </el-collapse-item>
        <div v-if="showAlertLock" class="alert-class">
          <el-alert
            :title="alertmsgLock"
            type="warning"
            show-icon
          ></el-alert>
        </div>
        <el-collapse-item name="accessResults">
          <template #title>
            <span>准入结果</span>&nbsp;&nbsp;&nbsp;
            <span v-if="showHistoryDateFlag" style="color: red;font-size: 12px">*截止到{{
                showHistoryDate
              }}年{{showHistoryMonth}}月{{showHistoryDay}}日数据</span>
          </template>
          <el-row>
            <el-col :span="6">
              <el-form-item label="实际准入形式" prop="actualAccessMethod">
                <div v-if="!showEdit" class="select-mask-wrapper">
                  <el-select
                    v-model="editForm.actualAccessMethod"
                    :disabled="actualAccessMethodDisabled"
                    ref="actualAccessMethod"
                    @change="handleActualAccessChange"
                    @clear="handleActualAccessClear"
                    :popper-append-to-body="false"
                    clearable
                  >
                    <el-option
                      v-for="[label, value] of filteredAdmissionStatusMap"
                      :key="label"
                      :label="label"
                      :value="label"
                      :disabled="isOptionDisabled(label, value)"
                    />
                  </el-select>
                  <div v-if="actualAccessMethodDisabled" class="click-mask"
                       @click="handleActualAccessMethodClick"></div>
                </div>
                <span v-else>
                  {{ editForm.actualAccessMethod || '&nbsp;' }}
                </span>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                label="实际准入时间（年/月/日）"
                prop="actualAdmissionDate"
              >
                <div v-if="!showEdit" class="select-mask-wrapper">
                  <el-date-picker
                    v-model="editForm.actualAdmissionDate"
                    style="width: 80%"
                    type="date"
                    prefix-icon="''"
                    popper-class="no-prefix-padding"
                    placeholder="请选择"
                    :disabled="actualAccessDisabled"
                    format="yyyy/MM/dd"
                    value-format="yyyy/MM/dd"
                    :picker-options="fixedPickerOptions"
                    @change="handleActualAdmissionDateChange"
                  ></el-date-picker>
                  <div v-if="actualAccessDisabled" class="click-mask" @click="handleActualAccessClick"></div>
                </div>
                <span v-else>
                  {{ editForm.actualAdmissionDate || '&nbsp;' }}
                </span>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                label="实际准入经销商（一级商）"
                prop="actualAdmissionDealer"
              >
                <div v-if="!showEdit" class="select-mask-wrapper">
                  <el-select
                    v-model="editForm.actualAdmissionDealer"
                    :disabled="actualAccessDisabled"
                    clearable
                    filterable
                    remote
                    :remote-method="remoteSearchActualAdmission"
                    :loading="dealerLoading"
                    @change="handleActualAdmissionDealerChange"
                    @clear="handleActualAdmissionDealerClear"
                  >
                    <el-option
                      v-for="option in commercialTerritory2"
                      :key="option.distributorCode"
                      :label="option.distributorCN"
                      :value="option.distributorCode"
                    ></el-option>
                    <template v-if="canLoadAllActualAdmissionFlag">
                      <div class="custom-empty">
                        <el-button
                          type="primary"
                          plain
                          class="load-more-btn"
                          @click="handleLoadAllActualAdmission"
                        >
                          展开全部选项
                        </el-button>
                      </div>
                    </template>
                  </el-select>
                  <div v-if="actualAccessDisabled" class="click-mask" @click="handleDistributorClick"></div>
                </div>
                <span v-else>
                  {{ editForm.actualAdmissionDealer || '&nbsp;' }}
                </span>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                label="配送经销商（一级商分子公司）"
                prop="deliveryDealer"
              >
                <div class="select-with-tip">
                  <div v-if="!showEdit" class="select-mask-wrapper">
                    <el-select
                      v-model="editForm.deliveryDealerCode"
                      :disabled="actualAccessDisabled"
                      filterable
                      clearable
                      @keyup.native="handleDeliveryDealerKeyUp"
                      @change="handleDeliveryDealerChange"
                      @clear="handleDeliveryDealerClear"
                    >
                      <el-option-group
                        v-for="group in deliveryDealerOptions"
                        :key="group.label"
                        :label="group.label"
                      >
                        <el-option
                          v-for="option in currentDistributorInfor"
                          :key="option.phCode"
                          :label="option.dealerName"
                          :value="option.phCode"
                        ></el-option>
                        <!-- 自定义空状态：替换"无匹配数据" -->
                        <template v-if="canLoadMore">
                          <div class="custom-empty">
                            <el-button
                              type="primary"
                              plain
                              class="load-more-btn"
                              @click="handleLoadMore"
                            >
                              展开全部选项
                            </el-button>
                          </div>
                        </template>
                      </el-option-group>
                    </el-select>
                    <div v-if="actualAccessDisabled" class="click-mask" @click="handleDistributorClick"></div>
                  </div>
                  <span v-else>
                    {{ editForm.deliveryDealer || '&nbsp;' }}
                  </span>
                </div>
                <div v-if="!showEdit" class="select-tip">
                  *仅限非一级商直配填写
                </div>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="6">
              <el-form-item
                label="实际准入商务一线"
                prop="actualAdmittanceSalesOne"
              >
                {{ editForm.actualCsfCn || '&nbsp;' }}
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="实际准入月份" prop="actualAdmittanceMonth">
                {{ editForm.actualAdmissionMonth || '&nbsp;' }}
              </el-form-item>
            </el-col>
          </el-row>
        </el-collapse-item>
        <el-collapse-item title="过程信息" name="processInfo">
          <el-row>
            <el-col :span="6">
              <el-form-item label="销售最新提单状态" prop="latestBlStatus">
                <div v-if="!showEdit" class="select-mask-wrapper">
                  <el-select
                    v-model="editForm.latestBlStatus"
                    :disabled="processInfoDisabled"
                    clearable
                  >
                    <el-option
                      v-for="[label, value] of recentSoStatusMap"
                      :key="value"
                      :label="label"
                      :value="value"
                    />
                  </el-select>
                  <div v-if="processInfoDisabled" class="click-mask" @click="handleActualAccessClick"></div>
                </div>
                <span v-else>
                  {{ editForm.latestBlStatus || '&nbsp;' }}
                </span>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="销售最新提单类型" prop="latestBlType">
                <div v-if="!showEdit" class="select-mask-wrapper">
                  <el-select
                    v-model="editForm.latestBlType"
                    :disabled="processInfoDisabled"
                    clearable
                  >
                    <el-option
                      v-for="[label, value] of recentSoTypeMap"
                      :key="value"
                      :label="label"
                      :value="value"
                    />
                  </el-select>
                  <div v-if="processInfoDisabled" class="click-mask" @click="handleActualAccessClick"></div>
                </div>
                <span v-else>
                  {{ editForm.latestBlType || '&nbsp;' }}
                </span>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                label="销售已提单/计划提单时间（年/月）"
                prop="planBillOfLadingTime"
              >
                <div v-if="!showEdit" class="select-mask-wrapper">
                  <el-cascader
                    style="width: 80%"
                    :props="defaultParams"
                    :show-all-levels="true"
                    v-model="billTimeType"
                    :options="monthYearYshOptions"
                    :disabled="processInfoDisabled"
                    @change="handleBillTimeTypeChange"
                    clearable
                  ></el-cascader>
                  <div v-if="processInfoDisabled" class="click-mask" @click="handleActualAccessClick"></div>
                </div>
                <span v-else>
                  {{ editForm.billTimeType || '&nbsp;' }}
                </span>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                label="商务确认提单有效性"
                prop="comfirmBillOfLading"
              >
                <div v-if="!showEdit" class="select-mask-wrapper">
                  <el-select
                    v-model="editForm.bizOrderValidation"
                    :disabled="processInfoDisabled"
                    clearable
                  >
                    <el-option
                      v-for="[label, value] of soValidatedMap"
                      :key="value"
                      :label="label"
                      :value="value"
                    />
                  </el-select>
                  <div v-if="processInfoDisabled" class="click-mask" @click="handleActualAccessClick"></div>
                </div>
                <span v-else>
                  {{ editForm.bizOrderValidation || '&nbsp;' }}
                </span>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="6">
              <el-form-item label="预估药事会时间" prop="estDtcDate">
                <div v-if="!showEdit" class="select-mask-wrapper">
                  <el-cascader
                    style="width: 80%"
                    :props="defaultParams"
                    :show-all-levels="true"
                    v-model="estDtcDate"
                    :options="monthYearOptions"
                    :disabled="processInfoDisabled"
                    @change="handleEstDtcDateChange"
                    clearable
                  ></el-cascader>
                  <div v-if="processInfoDisabled" class="click-mask" @click="handleActualAccessClick"></div>
                </div>
                <span v-else>
                  {{ editForm.estDtcDate || '&nbsp;' }}
                </span>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="预估准入形式" prop="predictAdmittanceForm">
                <div v-if="!showEdit" class="select-mask-wrapper">
                  <el-select
                    v-model="editForm.estAccessMethod"
                    :disabled="processInfoDisabled"
                    clearable
                  >
                    <el-option
                      v-for="[label, value] of admissionStatusMap"
                      :key="label"
                      :label="label"
                      :value="label"
                    />
                  </el-select>
                  <div v-if="processInfoDisabled" class="click-mask" @click="handleActualAccessClick"></div>
                </div>
                <span v-else>
                  {{ editForm.estAccessMethod || '&nbsp;' }}
                </span>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                label="预估准入时间（年/月）"
                prop="estAdmissionTime"
              >
                <div v-if="!showEdit" class="select-mask-wrapper">
                  <el-cascader
                    style="width: 80%"
                    :props="defaultParams"
                    :show-all-levels="true"
                    v-model="estAdmissionTime"
                    :options="monthYearYshOptions"
                    :disabled="processInfoDisabled"
                    @change="handleEstAdmissionTimeChange"
                    clearable
                  ></el-cascader>
                  <div v-if="processInfoDisabled" class="click-mask" @click="handleActualAccessClick"></div>
                </div>
                <span v-else>
                  {{ editForm.estAdmissionTime || '&nbsp;' }}
                </span>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="倒车填报原因" prop="reverseFillReason">
                <div class="select-with-tip">
                  <div v-if="!showEdit" class="select-mask-wrapper">
                    <el-select
                      v-model="editForm.reverseDeclarationReason"
                      :disabled="reverseReasonDisabled"
                      clearable
                    >
                      <el-option
                        v-for="[label, value] of reverseEntryReasonMap"
                        :key="value"
                        :label="label"
                        :value="value"
                      />
                    </el-select>
                    <div v-if="reverseReasonDisabled" class="click-mask" @click="handleReverseFillReason"></div>
                  </div>
                  <span v-else>
                  {{ editForm.reverseDeclarationReason || '&nbsp;' }}
                </span>
                </div>
                <div v-if="!showEdit" class="select-tip">
                  *仅限填报错误更正使用
                </div>
              </el-form-item>
            </el-col>
          </el-row>
        </el-collapse-item>
        <el-collapse-item title="认定准入结果" name="determineAccessResults">
          <el-row>
            <el-col :span="6">
              <el-form-item
                label="实际准入形式"
                prop="actualAdmittanceForm">
                {{ editForm.confirmActualAccessMethod || '' }}
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                label="实际准入时间（年/月/日）"
                prop="actualAdmittanceTime"
              >
                <div @click="handleDetermineAccessResultsClick"> {{
                    editForm.confirmActualAdmissionDate || '&nbsp;'
                  }}
                </div>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                label="实际准入经销商（一级商）"
                prop="actualAdmittanceDistributor"
              >
                <div @click="handleDetermineAccessResultsClick">{{
                    editForm.confirmActualAdmissionDealer || '&nbsp;'
                  }}
                </div>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                label="配送经销商（一级商分子公司）"
                prop="distributionDistributors"
              >
                <div @click="handleDetermineAccessResultsClick">{{
                    confirmAccessResults.deliveryDealer || '&nbsp;'
                  }}
                </div>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="6">
              <el-form-item
                label="实际准入商务一线"
                prop="actualAdmittanceSalesOne"
              >
                {{ editForm.confirmActualCsfCn || '&nbsp;' }}
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="实际准入月份" prop="actualAdmittanceMonth">
                {{ editForm.confirmActualAdmissionMonth || '&nbsp;' }}
              </el-form-item>
            </el-col>
          </el-row>
        </el-collapse-item>
        <el-collapse-item title="历史准入结果" name="historicalAccessResults">
          <el-row>
            <el-col :span="6">
              <el-form-item label="实际准入形式" prop="actualAdmittanceForm">
                {{ editForm.historyActualAccessMethod || '&nbsp;' }}
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                label="实际准入时间（年/月/日）"
                prop="actualAdmittanceTime"
              >
                {{ editForm.historyActualAdmissionDate || '&nbsp;' }}
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                label="实际准入经销商（一级商）"
                prop="actualAdmittanceDistributor"
              >
                {{ editForm.historyActualAdmissionDealer || '&nbsp;' }}
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                label="配送经销商（一级商分子公司）"
                prop="distributionDistributors"
              >
                {{ historicalAccessResults.deliveryDealer || '&nbsp;' }}
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="6">
              <el-form-item
                label="实际准入商务一线"
                prop="actualAdmittanceSalesOne"
              >
                {{ editForm.historyActualCsfCn || '&nbsp;' }}
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="实际准入月份" prop="actualAdmittanceMonth">
                {{ editForm.historyActualAdmissionMonth || '&nbsp;' }}
              </el-form-item>
            </el-col>
          </el-row>
        </el-collapse-item>
        <el-collapse-item title="经销商及人员信息" name="dealerPersonnelInfo">
          <el-row>
            <el-col :span="6">
              <el-form-item
                label="项目支持经销商"
                prop="projectSupportDistributor"
              >
                <div v-if="!showEdit" class="select-mask-wrapper">
                  <el-select
                    v-model="editForm.distributorCode"
                    filterable
                    :disabled="dealerPersonnelDisabled"
                    @change="handleDistributorCnChange"
                    @clear="handleDistributorCnClear"
                    clearable
                  >
                    <el-option
                      v-for="option in commercialTerritory"
                      :key="option.distributorCode"
                      :label="option.distributorCN"
                      :value="option.distributorCode"
                    ></el-option>
                  </el-select>
                  <div v-if="dealerPersonnelDisabled" class="click-mask" @click="handleDistributorCnClick"></div>
                </div>
                <span v-else>
                  {{ editForm.distributorCn || '&nbsp;' }}
                </span>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                label="项目支持经销商所属集团"
                prop="distributorGroup"
              >
                {{ editForm.projectSupportDealerGroup || '&nbsp;' }}
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="商务区域" prop="businessArea">
                {{ editForm.comRegionCn || '&nbsp;' }}
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="商务一线" prop="businessFrontline">
                {{ editForm.csfCn || '&nbsp;' }}
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="6">
              <el-form-item label="销售区域对接人" prop="regionalDockingPerson">
                {{ editForm.mrName || '&nbsp;' }}
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="销售 Cluster" prop="salesCluster">
                {{ editForm.cluster || '&nbsp;' }}
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="销售大区" prop="salesRegion">
                {{ editForm.district || '&nbsp;' }}
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="销售 RM Name" prop="salesRMName">
                {{ editForm.rsmName || '&nbsp;' }}
              </el-form-item>
            </el-col>
          </el-row>
        </el-collapse-item>
        <el-collapse-item title="变更记录" name="changeRecord" v-if="showEdit">
          <el-table v-loading="tableLoading" :data="changeRecordList" @sort-change="tableSortChange"
                    style="width: 100%">
            <el-table-column prop="updateFieldNameCn" label="字段名" sortable="custom"/>
            <el-table-column prop="oldValue" label="更改前"/>
            <el-table-column prop="newValue" label="更改后"/>
            <el-table-column prop="updateTimeStr" label="日期"/>
            <el-table-column prop="updateUserName" label="用户"/>
          </el-table>
          <el-pagination
            background
            :current-page="currentPage"
            :page-sizes="[5, 10, 20]"
            :page-size="pageSize"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </el-collapse-item>
      </el-collapse>
    </el-form>
    <el-dialog
      :visible.sync="dialogVisibleDiffDealer"
      width="25%"
      :close-on-click-modal="false"
      custom-class="dialogstyle"
      :modal="false"
      @close="diffDealerCancle"
    >
      医院省份与经销商省份不同，请确认是否选择正确。
      <div slot="footer">
        <el-button class="cancel" @click="diffDealerCancle">取消</el-button>
        <el-button class="confirm" @click="diffDealerSubmit">确认</el-button>
      </div>
    </el-dialog>
    <el-dialog
      :visible.sync="dialogVisibleReverseFillReason"
      width="25%"
      :close-on-click-modal="false"
      custom-class="dialogstyle"
      :modal="false"
      @close="cancelReverseForm"
    >
      <div style="line-height: 40px">您在倒车填报 {{ currentTitle }}，</div>
      <div style="line-height: 40px">
        请选择倒车原因：
        <el-form :model="editForm" :rules="rules" ref="reverseForm">
          <el-form-item prop="reverseDeclarationReason">
            <el-select
              v-model="editForm.reverseDeclarationReason"
              :popper-append-to-body="false"
              clearable
              style="width: 50%"
            >
              <el-option
                v-for="[label, value] of reverseEntryReasonMap"
                :key="value"
                :label="label"
                :value="value"
              />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <div style="line-height: 40px">
        将实际准入形式{{ defaultActualAccessMethod }}修改为
        {{ editForm.actualAccessMethod }}
      </div>

      <div slot="footer">
        <el-button class="cancel" @click="cancelReverseForm">取消</el-button>
        <el-button class="confirm" @click="verifyData('reverseForm')">
          提交
        </el-button>
      </div>
    </el-dialog>
    <div id="button" slot="footer" class="custom-footer" v-if="!showEdit">
      <el-button
        class="cancel"
        style="margin-right: 80%"
        @click="closeDialog(true)"
      >
        关闭
      </el-button>
      <el-button
        class="confirm"
        :loading="saveLoading"
        :disabled="submitDisabled"
        @click="submitProduct('editForm')"
      >
        提交
      </el-button>
    </div>
  </div>
</template>

<script>
import moment from 'moment-timezone'

export default {
  name: 'ProductAccessInfo',
  props: {
    showEdit: {
      type: Boolean,
      default: false,
    },
    currentTitle: {
      type: String,
      default: '',
    },
    selectData: {
      type: Object,
      default: () => {
      },
    },
    admissionStatusMap: {
      type: Map,
    },
    recentSoStatusMap: {
      type: Map,
    },
    recentSoTypeMap: {
      type: Map,
    },
    reverseEntryReasonMap: {
      type: Map,
    },
    soValidatedMap: {
      type: Map,
    },
    commercialTerritory: {
      type: Array,
    },
    commercialTerritory2: {
      type: Array,
    },
    distributorInfor: {
      type: Array,
    },
    attentionLevel: {
      type: String,
      default: '',
    }
  },
  data() {
    return {
      currentActualCsf: {},
      defaultDistributorCode: null,
      defaultDistributorCn: null,
      futureMonth: [],
      withoutRules: false,
      currentQuarter: 0,
      terminalName: null,
      terminalCode: null,
      terminalLevel: null,
      terminalProvince: null,
      defaultActualAccessMethod: null,
      editForm: {
        id: null,
        projectId: null,
        terminalCode: null,
        productCode: null,
        actualAccessMethod: null,
        actualAdmissionDate: null,
        actualAdmissionDealer: null,
        actualAdmissionDealerCode: null,
        deliveryDealer: null,
        deliveryDealerCode: null,
        actualCsfCn: null,
        actualCsfCode: null,
        actualAdmissionMonth: null,
        latestBlStatus: null,
        latestBlType: null,
        billTimeType: null,
        bizOrderValidation: null,
        estAdmissionTime: null,
        estDtcDate: null,
        estAccessMethod: null,
        reverseDeclarationReason: null,
        distributorCode: null,
        distributorCn: null,
        projectSupportDealerGroup: null,
        comRegionCn: null,
        csfCode: null,
        csfCn: null,
        mrCode: null,
        mrName: null,
        clusterCode: null,
        cluster: null,
        district: null,
        rsmCode: null,
        rsmName: null,
        confirmActualAccessMethod: null,
        confirmActualAdmissionDate: null,
        confirmActualAdmissionDealer: null,
        confirmActualAdmissionDealerCode: null,
        comfirmDeliveryDealer: null,
        comfirmDeliveryDealerCode: null,
        confirmActualCsfCn: null,
        confirmActualCsfCode: null,
        confirmActualAdmissionMonth: null,
        historyActualAccessMethod: null,
        historyActualAdmissionDate: null,
        historyActualAdmissionDealer: null,
        historyActualAdmissionDealerCode: null,
        historyActualCsfCn: null,
        historyActualCsfCode: null,
        historyActualAdmissionMonth: null,
        provinceInconsistent: false,
        distributorProvince: null,
        attentionLevel: null,
      },
      billTimeType: [],
      estAdmissionTime: [],
      estDtcDate: [],
      confirmAccessResults: {
        actualAccessMethod: null,
        actualAdmissionDate: null,
        actualAdmissionDealer: null,
        actualAdmissionDealerCode: null,
        deliveryDealer: null,
        deliveryDealerCode: null,
        actualCsfCn: null,
        actualCsfCode: null,
        actualAdmissionMonth: null,
      },
      historicalAccessResults: {
        actualAccessMethod: null,
        actualAdmissionDate: null,
        actualAdmissionDealer: null,
        actualAdmissionDealerCode: null,
        deliveryDealer: null,
        deliveryDealerCode: null,
        actualCsfCn: null,
        actualCsfCode: null,
        actualAdmissionMonth: null,
      },
      productAccessList: [
        {
          accessState: '',
          isBillOfLading: '',
          billOfLadingRanking: '',
          actualAdmittanceTime: '',
          productAccessWay: '',
          quarterHospitalControl: '',
          quarterAggregateRatio: '',
        },
      ],

      changeRecordList: [],
      monthYearWeekOptions: [],
      monthYearOptions: [],
      monthYearYshOptions: [],
      initDropdownData: {},
      loginUser: JSON.parse(sessionStorage.getItem('userInfo')).userInfo.id,
      sessionStorage: sessionStorage,
      loading: false,
      formLoding: true,
      filterLoding: true,
      tagLoading: false,
      saveLoading: false,
      activeNames: [
        'basicInfo',
        'accessResults',
        'processInfo',
        'determineAccessResults',
        'historicalAccessResults',
        'dealerPersonnelInfo',
        'changeRecord',
      ],
      dialogVisibleDiffDealer: false,
      dialogVisibleReverseFillReason: false,
      dialogReverseFillReason: {
        reverseEntryReason: null,
        actualAccessMethod: null,
        currentAccessMethod: null,
      },
      currentPage: 1,
      pageSize: 5,
      total: 0,
      dealerPersonnelDisabled: false,
      processInfoDisabled: false,
      actualAccessMethodDisabled: false,
      actualAccessDisabled: false,
      reverseReasonDisabled: true,
      showAlert: false,
      showAlert1: false,
      showAlert2: false,
      showAlert3: false,
      showAlertLock: false,
      alertmsgLock: '',
      alertmsg: null,
      alertmsg1: '如准入经销商信息与已认定信息不符，请联系：ivy.huang@bayer.com',
      alertmsg2: '该条目为已认定的非正式准入，只有将“实际准入形式”调整至“正式准入"时，才可对其他准入结果字段进行编辑',
      alertmsg3: '该条目是过往项目的非正式准入',
      defaultParams: {
        id: 'id',
        label: 'label',
        value: 'value',
        children: 'children',
        leaf: true,
      },
      role: '',
      userRoles: [],
      rules: {
        reverseDeclarationReason: [
          {required: true, message: '请选择倒车原因', trigger: 'change'},
        ],
      },
      formRules: {
        actualAccessMethod: [
          {required: false, message: '请选择实际准入形式', trigger: ['change', 'blur']},
        ],
        actualAdmissionDate: [
          {required: false, message: '请选择实际准入时间', trigger: ['change', 'blur']},
        ],
        actualAdmissionDealer: [
          {required: false, message: '请选择实际准入经销商', trigger: ['change', 'blur']},
        ],
      },
      deliveryDealerOptions: [
        {
          label: '全部数据',
        },
      ],
      canLoadAllActualAdmissionFlag: true,
      canLoadMore: false,
      fixedPickerOptions: null,
      currentDistributorInfor: [],
      historyOrderBy: null,
      tableLoading: false,
      tmpProvinceCN: null,
      editDistributor: false,
      currentData: {},
      submitDisabled: false,
      showHistoryDateFlag: false, //是否需要展示历史数据提示
      showHistoryDate: null,
      showHistoryMonth: null,
      showHistoryDay: null,
      notAllowedReverseFlag: false, //是否允许倒车标志
    }
  },
  computed: {
    // 使用计算属性动态处理数据
    processedchangeRecordList() {
      // 首先过滤出所有需要置顶的条目
      const topItems = this.changeRecordList.filter(
        (item) => item.updateFieldNameCn === '实际准入形式',
      )
      // 然后过滤出所有普通条目
      const normalItems = this.changeRecordList.filter(
        (item) => item.updateFieldNameCn !== '实际准入形式',
      )
      // 将置顶条目放在数组前面，普通条目放在后面
      return [...topItems, ...normalItems]
    },
    filteredAdmissionStatusMap() {
      let map = this.admissionStatusMap || {}
      const entries = Array.isArray(map) ? map : Object.entries(map || {})
      const res = entries.filter(([label]) => {
        return label && typeof label === 'string' && !label.includes('往期')
      })
      return res
    }
  },
  watch: {
    /*'editForm.actualAdmissionDate': {
      handler(newVal, oldVal) {
        if (newVal) {
          console.log('date cleared')
          const dateVal = new Date(newVal)
          const yearMonth =
            dateVal.getFullYear().toString() +
            '/' +
            (dateVal.getMonth() + 1).toString().padStart(2, '0')
          this.editForm.actualAdmissionMonth = yearMonth.toString()
        } else {
          this.editForm.actualAdmissionMonth = ''
        }

      },
      deep: true,
    },*/
    'editForm.actualAdmissionDate'(newVal) {
      if (!newVal) {
        if (this.editForm.actualAdmissionDate == null && this.editForm.actualAdmissionDealer == '' && this.editForm.actualAccessMethod == '') {
          this.formRules.actualAccessMethod[0].required = false
          this.formRules.actualAdmissionDate[0].required = false
          this.formRules.actualAdmissionDealer[0].required = false
        }
        this.editForm.actualAdmissionMonth = ''
        this.editForm.actualCsfCode = ''
        this.editForm.actualCsfCn = ''
        return
      }
      const [year, month] = newVal.split('/')
      this.editForm.actualAdmissionMonth = `${year}/${month}`
    },
    'editForm.actualAdmittanceDistributor': {
      handler(newVal, oldVal) {
        if (newVal != null && newVal != undefined) {
          this.editForm.actualAdmittanceDistributor = newVal
        }
      },
      deep: true,
    },
    'editForm.changeRecordList': {
      handler(newVal, oldVal) {
      },
      deep: true,
    },
    'editForm.estDtcDate': {
      handler(newVal, oldVal) {
        if (newVal != null && newVal != undefined)
          this.estDtcDate = newVal.split('/')
      },
      deep: true,
    },
    'editForm.estAdmissionTime': {
      handler(newVal, oldVal) {
        if (newVal != null && newVal != undefined)
          this.estAdmissionTime = newVal.split('/')
      },
      deep: true,
    },
    'editForm.billTimeType': {
      handler(newVal, oldVal) {
        if (newVal != null && newVal != undefined)
          this.billTimeType = newVal.split('/')
      },
      deep: true,
    },
    attentionLevel(val) {
      this.editForm.attentionLevel = val
    },
  },
  created() {
    let {userRoles} = JSON.parse(sessionStorage.getItem('userInfo'))
    this.userRoles = userRoles
    if (
      this.userRoles.filter((item) => {
        return item.roleName.indexOf('管理员') > -1
      }).length > 0
    ) {
      this.role = '1'
    } else if (
      this.userRoles.filter((item) => {
        return item.roleName == '商务一线'
      }).length > 0
    ) {
      this.role = '2'
    } else if (
      this.userRoles.filter((item) => {
        return item.roleName == '固定负责人'
      }).length > 0
    ) {
      this.role = '3'
    } else if (
      this.userRoles.filter((item) => {
        return item.roleName == '商务经理'
      }).length > 0
    ) {
      this.role = '4'
    } else if (
      this.userRoles.filter((item) => {
        return item.roleName == '商务总监'
      }).length > 0
    ) {
      this.role = '5'
    }
  },
  mounted() {
    this.getProductAdmissionListFilterOptions()
    this.getProductAdmission()
    this.getHistoryChangeRecord()
    this.getYearMonth()

    // this.getCurrentYearMonthsList()
    // this.getYearMonthWeek()
    // this.getMonthOption()
  },
  methods: {
    accessState(value, index) {
      if (value != '正式准入') {
        this.editForm.productAccessList[index].productAccessWay = ''
      }
    },
    recommended(selectedCode) {
      const queryParams = {tierOneCode: selectedCode}
      this.$apiFetch
        .get(`/acc/productAdmission/getTierTwoByTierOneCode`, queryParams)
        .then((res) => {
          if (res && res.code == 200) {
            console.log(res.data)
            if (res.data && res.data.length > 0) {
              this.canLoadMore = true
              this.currentDistributorInfor = res.data
              this.deliveryDealerOptions[0].label = '为你推荐'
              const isNotIncluded = !res.data.some(data => data.distributorCode === this.editForm.deliveryDealerCode)
              if (isNotIncluded) {
                this.editForm.deliveryDealerCode = ''
                this.editForm.deliveryDealer = ''
              }
            }
          }
        })
        .catch((error) => {
          console.log(error)
        })
    },
    initDistributorInfor() {
      if (this.editForm.actualAdmissionMonth && this.editForm.actualAdmissionDealerCode) {
        const monthArr = this.editForm.actualAdmissionMonth.split('/')
        const queryParams = {
          distributorCode: this.editForm.actualAdmissionDealerCode,
          year: monthArr[0],
          month: monthArr[1],
        }
        this.$apiFetch
          .get(`/acc/productAdmission/getCSFInformation`, queryParams)
          .then((res) => {
            if (res && res.code == 200 && res.data) {
              console.log(res.data)
              this.editForm.actualCsfCode = res.data.csfCode
              this.editForm.actualCsfCn = res.data.csfCN
            } else {
              this.editForm.actualCsfCode = ''
              this.editForm.actualCsfCn = ''
              this.alertmsg = '无法获取该准入经销商对应的当前实际准入商务一线。'
              this.showAlert = true
              // this.$message.error('无法获取该准入经销商对应的当前实际准入商务一线。')
            }
          })
          .catch((error) => {
            console.log(error)
          })
      }
    },
    handleActualAdmissionDealerChange(selectedCode) {
      this.handleSubmitDisabled()
      if (selectedCode === null || selectedCode === '' || selectedCode === undefined) {
        this.editForm.actualAdmissionDealer = ''
        return
      }
      this.formRules.actualAccessMethod[0].required = true
      this.formRules.actualAdmissionDate[0].required = true
      this.formRules.actualAdmissionDealer[0].required = true
      this.showHistoryDateFlag = false
      //获取经销商省份
      //判断经销商省份跟医院省份是否一致
      //如果不一致，则弹出确认框
      /**
       * （1）在提交修改时需要弹提示框
       * （2）数据校验：不同时间字段的校验(预估准入时间(年/月), 实际准入月份等)
       * （3）通知：外省商务负责此填报
       * （4）权限：给予项目支持经销商对应商务查看权限
       */
      // this.showAlert = this.actualAccessDisabled
      // this.alertmsg = '准入经销商信息与已认定信息不符，请联系XXX..'
      if (selectedCode) {
        const selectedObj = this.commercialTerritory2.find(
          (item) => item.distributorCode === selectedCode,
        )
        // this.editForm.actualCsfCode = selectedObj.csfCode
        // this.editForm.actualCsfCn = selectedObj.csfCN
        this.editForm.actualAdmissionDealer = selectedObj.distributorCN
        this.editForm.actualAdmissionDealerCode = selectedObj.distributorCode
      } else {
        this.editForm.actualCsfCode = ''
        this.editForm.actualCsfCn = ''
        this.editForm.actualAdmissionDealer = ''
        this.editForm.actualAdmissionDealerCode = ''
      }
      this.recommended(selectedCode)
      this.initDistributorInfor()

    },
    async handleLoadAllActualAdmission() {
      try {
        const res = await this.getAllActualAdmissionDealers()
        if (res && res.code === 200) {
          this.commercialTerritory2 = res.data || []
          this.canLoadAllActualAdmissionFlag = false
        }
      } catch (error) {
        console.log(error)
      }
    },
    async remoteSearchActualAdmission(query) {
      this.canLoadAllActualAdmissionFlag = false
      if (!this.hasLoadedAllDealers) {
        this.dealerLoading = true
        try {
          const res = await this.getAllActualAdmissionDealers()
          if (res && res.code === 200) {
            this.allCommercialTerritory = res.data || []
            this.hasLoadedAllDealers = true
          }
        } finally {
          this.dealerLoading = false
        }
      }
      if (!query) {
        this.commercialTerritory2 = [...this.allCommercialTerritory]
        return
      }
      const keyword = query.toLowerCase()
      this.commercialTerritory2 = this.allCommercialTerritory.filter(item =>
        item.distributorCN?.toLowerCase().includes(keyword) ||
        item.distributorCode?.toLowerCase().includes(keyword)
      )
    },
    getAllActualAdmissionDealers() {
      return this.$apiFetch.get(
        `/acc/productAdmission/getadmissionDealerByProvince`,
        {
          province: this.terminalProvince,
          type: 'all'
        }
      )
    },
    handleLoadMore() {
      this.currentDistributorInfor = this.distributorInfor
      this.deliveryDealerOptions[0].label = '全部数据'
      this.canLoadMore = false
    },
    handleDeliveryDealerChange(selectedCode) {
      this.showHistoryDateFlag = false
      if (selectedCode === null || selectedCode === '' || selectedCode === undefined) {
        this.editForm.deliveryDealer = ''
        return
      }
      // this.showAlert = this.actualAccessDisabled
      // this.alertmsg = '准入经销商信息与已认定信息不符，请联系XXX..'
      this.formRules.actualAccessMethod[0].required = true
      this.formRules.actualAdmissionDate[0].required = true
      this.formRules.actualAdmissionDealer[0].required = true
      if (selectedCode) {
        const selectedObj = this.distributorInfor.find(
          (item) => item.phCode === selectedCode,
        )
        this.editForm.deliveryDealer = selectedObj.dealerName
      } else {
        this.editForm.deliveryDealer = ''
      }
    },
    handleActualAdmissionDealerClear() {
      if (this.editForm.actualAdmissionDate == null && this.editForm.actualAdmissionDealer == '' && this.editForm.actualAccessMethod == '') {
        this.formRules.actualAccessMethod[0].required = false
        this.formRules.actualAdmissionDate[0].required = false
        this.formRules.actualAdmissionDealer[0].required = false
      }
      this.editForm.actualAdmissionDealerCode = ''
      this.editForm.actualCsfCode = ''
      this.editForm.actualCsfCn = ''
      this.showHistoryDateFlag = false
    },
    handleDeliveryDealerClear() {
      this.showHistoryDateFlag = false
      this.currentDistributorInfor = this.distributorInfor
      this.deliveryDealerOptions[0].label = '全部数据'
      this.canLoadMore = false
      if (this.editForm.actualAdmissionDate == null && this.editForm.actualAdmissionDealer == '' && this.editForm.actualAccessMethod == '') {
        this.formRules.actualAccessMethod[0].required = false
        this.formRules.actualAdmissionDate[0].required = false
        this.formRules.actualAdmissionDealer[0].required = false
      }
    },
    handleDeliveryDealerKeyUp(event) {
      this.currentDistributorInfor = this.distributorInfor
      this.deliveryDealerOptions[0].label = '全部数据'
      this.canLoadMore = false
    },
    handleDistributorCnClick() {
      this.alertmsg =
        '如准入经销商信息与已认定信息不符，请联系：ivy.huang@bayer.com'
      // this.showAlert = this.dealerPersonnelDisabled
    },
    handleDistributorCnChange(selectedCode) {
      if (selectedCode) {
        const selectedObj = this.commercialTerritory.find(
          (item) => item.distributorCode === selectedCode,
        )
        this.editDistributor = false
        this.currentActualCsf = selectedObj
        this.tmpProvinceCN = selectedObj.provinceCN
        if (this.terminalProvince !== selectedObj.provinceCN) {
          this.editForm.provinceInconsistent = true
          this.dialogVisibleDiffDealer = true
        } else {
          this.editForm.provinceInconsistent = false
          this.diffDealerSubmit()
        }
      } else {
        this.editForm.distributorCn = ''
        this.editForm.distributorCode = ''
        this.editForm.projectSupportDealerGroup = ''
        this.editForm.comRegionCn = ''
        this.editForm.csfCn = ''
        this.editForm.csfCode = ''
        this.editForm.distributorProvince = ''
      }
    },
    handleDistributorCnClear() {
      this.editForm.distributorProvince = ''
    },
    handleAccessDealerChange(selectedVal) {
      this.alertmsg =
        '如准入经销商信息与已认定信息不符，请联系：ivy.huang@bayer.com'
      // this.showAlert = this.processInfoDisabled
      // if (selectedVal) {
      //   // const selectedObj = [...this.admissionStatusMap].find(([key, value]) => value === selectedVal)
      //   // this.actualAccessMethodName = selectedObj[0]
      //   this.dialogReverseFillReason.currentAccessMethod = selectedVal
      //   this.dialogReverseFillReason.actualAccessMethod =
      //     this.editForm.actualAccessMethod
      //   if (this.editForm.actualAccessMethod !== selectedVal) {
      //     if (this.editForm.actualAccessMethod === '正式准入') {
      //       this.dialogVisibleReverseFillReason = true
      //     } else if (
      //       this.editForm.actualAccessMethod === '临采' &&
      //       selectedVal === 'Blank'
      //     ) {
      //       this.dialogVisibleReverseFillReason = true
      //     } else if (
      //       this.editForm.actualAccessMethod === '批量临采' &&
      //       selectedVal === 'Blank'
      //     ) {
      //       this.dialogVisibleReverseFillReason = true
      //     }
      //   }
      // }
    },
    handleActualAccessClick() {
      // this.showAlert = this.actualAccessDisabled
      // if (this.role === '2') {
      //   this.alertmsg =
      //     '这条是已认定正式/非正式准入记录，如果需要修改字段，请联系：ivy.huang@bayer.com'
      // } else {
      this.alertmsg =
        '如对原有认定结果进行调整，请联系：ivy.huang@bayer.com'
      // }
    },
    handleDistributorClick() {
      // this.showAlert = this.actualAccessDisabled
      this.alertmsg =
        '如准入经销商信息与已认定信息不符，请联系：ivy.huang@bayer.com'

    },
    handleActualAccessMethodClick() {
      // this.showAlert = this.actualAccessMethodDisabled
      // if (this.role === '2') {
      //   this.alertmsg =
      //     '这条是已认定正式/非正式准入记录，如果需要修改字段，请联系：ivy.huang@bayer.com'
      // } else {
      this.alertmsg =
        '如对原有认定结果进行调整，请联系：ivy.huang@bayer.com'
      // }
    },
    handleDetermineAccessResultsClick() {
      // this.showAlert = this.dealerPersonnelDisabled
      // if (this.role === '2') {
      //   this.alertmsg =
      //     '这条是已认定正式/非正式准入记录，如果需要修改字段，请联系：ivy.huang@bayer.com'
      // } else {
      this.alertmsg =
        '如对原有认定结果进行调整，请联系：ivy.huang@bayer.com'
      // }
    },
    handleActualAccessChange(val) {
      if (val !== this.defaultActualAccessMethod) {
        this.showHistoryDateFlag = false
        this.actualAccessDisabled = false
        if (this.defaultActualAccessMethod === '正式准入') {
          this.reverseReasonDisabled = false
        } else if (
          this.defaultActualAccessMethod === '临采' &&
          (this.editForm.actualAccessMethod === 'Blank' ||
            this.editForm.actualAccessMethod === '' ||
            this.editForm.actualAccessMethod === null)
        ) {
          this.reverseReasonDisabled = false
        } else if (
          this.defaultActualAccessMethod === '批量临采' &&
          (this.editForm.actualAccessMethod === 'Blank' ||
            this.editForm.actualAccessMethod === '' ||
            this.editForm.actualAccessMethod === null)
        ) {
          this.reverseReasonDisabled = false
        }
      }
      if (val === '正式准入') {
        this.reverseReasonDisabled = true
        if (this.selectData.isConfirmed && this.defaultActualAccessMethod !== '正式准入') {
          this.actualAccessDisabled = false
        }
      } else {
        if (this.selectData.isConfirmed && this.defaultActualAccessMethod !== '正式准入') {
          this.actualAccessDisabled = true
          this.editForm.actualAdmissionDate = this.currentData.actualAdmissionDate
          this.editForm.actualAdmissionDealer = this.currentData.actualAdmissionDealer
          this.editForm.actualAdmissionDealerCode = this.currentData.actualAdmissionDealerCode
          this.editForm.deliveryDealer = this.currentData.deliveryDealer
          this.editForm.deliveryDealerCode = this.currentData.deliveryDealerCode
          this.editForm.actualCsfCn = this.currentData.actualCsfCn
          this.editForm.actualCsfCode = this.currentData.actualCsfCode
          this.editForm.actualAdmissionMonth = this.currentData.actualAdmissionMonth
        }
      }
      if (val) {
        this.formRules.actualAccessMethod[0].required = true
        this.formRules.actualAdmissionDate[0].required = true
        this.formRules.actualAdmissionDealer[0].required = true
      } else {
        this.formRules.actualAccessMethod[0].required = false
        this.formRules.actualAdmissionDate[0].required = false
        this.formRules.actualAdmissionDealer[0].required = false
      }
      if (val === 'Blank') {
        this.handleActualAccessClear()
      }
      this.handleSubmitDisabled()
    },
    handleActualAccessClear() {
      this.showHistoryDateFlag = false
      this.editForm.actualAdmissionDate = ''
      this.editForm.actualAdmissionDealer = null
      this.editForm.actualAdmissionDealerCode = ''
      this.editForm.deliveryDealer = ''
      this.editForm.deliveryDealerCode = ''
      this.editForm.actualCsfCn = ''
      this.editForm.actualCsfCode = ''
      this.editForm.actualAdmissionMonth = ''
      this.formRules.actualAccessMethod[0].required = false
      this.formRules.actualAdmissionDate[0].required = false
      this.formRules.actualAdmissionDealer[0].required = false
      this.$nextTick(() => {
        this.$refs.formRef?.clearValidate([
          'actualAccessMethod',
          'actualAdmissionDate',
          'actualAdmissionDealer'
        ])
      })
    },
    handleSubmitDisabled() {
      this.submitDisabled = false
      if (this.editForm.actualAdmissionDate || this.editForm.actualAdmissionDealer) {
        if (!this.editForm.actualAccessMethod || this.editForm.actualAccessMethod === 'Blank') {
          this.submitDisabled = true
        }
      }
    },
    handleActualAdmissionDateChange(val) {
      this.showHistoryDateFlag = false
      this.formRules.actualAccessMethod[0].required = true
      this.formRules.actualAdmissionDate[0].required = true
      this.formRules.actualAdmissionDealer[0].required = true
      this.handleSubmitDisabled()
      if (
        this.formatDate(this.editForm.actualAdmissionDate) ===
        this.formatDate(this.editForm.historyActualAdmissionDate)
      ) {
        // this.showAlert = true
        this.alertmsg = '请商务同事更新正式准入日期'
      } else {
        // this.showAlert = false
      }
      if (val != null) {
        this.initDistributorInfor()
      }
    },
    formatDate(str) {
      if (str) {
        const date = new Date(str)
        return (
          date.getFullYear().toString() +
          '/' +
          (date.getMonth() + 1).toString().padStart(2, '0') +
          '/' +
          date.getDate().toString().padStart(2, '0')
        )
      }
    },
    getProductAdmission() {
      this.formLoding = true
      const queryParams = {
        productAdmissionId: this.selectData.id,
        projectId: this.selectData.projectId,
        terminalCode: this.selectData.terminalCode,
        productCode: this.selectData.productCode,
      }
      this.$apiFetch
        .post(`/acc/productAdmission/getProductAdmission`, queryParams)
        .then((res) => {
          if (res && res.code == 200) {
            if (res.data.extendLastAdmission && res.data.createOrExtendFlag === 0) {
              if (res.data.hasRecord === 0) {
                this.showHistoryDateFlag = true
                this.showHistoryDate = res.data.parentDate.substring(0, 4)
                this.showHistoryMonth = res.data.parentDate.substring(5, 7)
                this.showHistoryDay = res.data.parentDate.substring(8, 10)
              }
              if (res.data.actualAccessMethod !== '正式准入') {
                this.showAlert3 = true
              }
            }
            if (res.data.actualAccessMethod !== '正式准入' && (res.data.lockAndConfirmed === '已锁定已认定'
              || res.data.lockAndConfirmed === '未锁定已认定'
              || (res.data.extendLastAdmission && res.data.createOrExtendFlag === 0 && res.data.parentIsConfirmed))) {
              this.notAllowedReverseFlag = true
            }
            /*if (res.data.actualAccessMethod !== '正式准入' && (res.data.lockAndConfirmed === '已锁定已认定' || res.data.lockAndConfirmed === '未锁定已认定')) {
              this.notAllowedReverseFlag = true
            }
            if (res.data.actualAccessMethod !== '正式准入' && (res.data.lockAndConfirmed === '已锁定已认定' || res.data.lockAndConfirmed === '未锁定已认定')) {
              this.notAllowedReverseFlag = true
            }*/
            this.currentData = res.data
            this.terminalCode = res.data.terminalCode
            this.terminalName = res.data.terminalName
            this.terminalLevel = res.data.terminalLevel
            this.terminalProvince = res.data.terminalProvince
            this.terminalCity = res.data.terminalCity
            this.terminalDistrict = res.data.terminalDistrict

            this.editForm.id = res.data.id
            this.editForm.projectId = res.data.projectId
            this.editForm.terminalCode = res.data.terminalCode
            this.editForm.productCode = res.data.productCode
            this.editForm.actualAccessMethod = res.data.actualAccessMethod
            this.editForm.lockAndConfirmed = res.data.lockAndConfirmed
            this.defaultActualAccessMethod = res.data.actualAccessMethod

            this.editForm.actualAdmissionDate = res.data.actualAdmissionDate
            this.editForm.actualAdmissionDealer =
              res.data.actualAdmissionDealer
            this.editForm.actualAdmissionDealerCode =
              res.data.actualAdmissionDealerCode
            this.editForm.actualCsfCn = res.data.actualCsfCn
            this.editForm.actualCsfCode = res.data.actualCsfCode
            this.editForm.actualAdmissionMonth = res.data.actualAdmissionMonth
            this.editForm.latestBlStatus = res.data.latestBlStatus
            this.editForm.latestBlType = res.data.latestBlType
            this.editForm.billTimeType = res.data.billTimeType
            this.editForm.bizOrderValidation = res.data.bizOrderValidation
            this.editForm.estAdmissionTime = res.data.estAdmissionTime
            this.editForm.estDtcDate = res.data.estDtcDate
            this.editForm.estAccessMethod = res.data.estAccessMethod
            this.editForm.reverseDeclarationReason =
              res.data.reverseDeclarationReason
            this.editForm.distributorCode = res.data.distributorCode
            this.editForm.distributorCn = res.data.distributorCn
            this.editForm.deliveryDealer = res.data.deliveryDealer
            this.editForm.deliveryDealerCode = res.data.deliveryDealerCode
            this.editForm.projectSupportDealerGroup =
              res.data.projectSupportDealerGroup
            this.editForm.comRegionCn = res.data.comRegionCn
            this.editForm.csfCode = res.data.csfCode
            this.editForm.csfCn = res.data.csfCn
            this.editForm.mrCode = res.data.mrCode
            this.editForm.mrName = res.data.mrName

            this.editForm.confirmActualAccessMethod =
              res.data.confirmActualAccessMethod

            this.editForm.confirmActualAdmissionDate =
              res.data.confirmActualAdmissionDate
            this.editForm.confirmActualAdmissionDealer =
              res.data.confirmActualAdmissionDealer
            this.editForm.confirmActualAdmissionDealerCode =
              res.data.confirmActualAdmissionDealerCode
            this.confirmAccessResults.deliveryDealer =
              res.data.confirmDeliveryDealer
            this.confirmAccessResults.deliveryDealerCode =
              res.data.confirmDeliveryDealerCode
            this.editForm.confirmActualCsfCode = res.data.confirmActualCsfCode
            this.editForm.confirmActualCsfCn = res.data.confirmActualCsfCn
            this.editForm.confirmActualAdmissionMonth =
              res.data.confirmActualAdmissionMonth

            this.editForm.historyActualAccessMethod =
              res.data.historyActualAccessMethod
            this.editForm.historyActualAdmissionDate =
              res.data.historyActualAdmissionDate
            this.editForm.historyActualAdmissionDealer =
              res.data.historyActualAdmissionDealer
            this.editForm.historyActualAdmissionDealerCode =
              res.data.historyActualAdmissionDealerCode
            this.historicalAccessResults.deliveryDealer =
              res.data.historyDeliveryDealer
            this.historicalAccessResults.deliveryDealerCode =
              res.data.historyDeliveryDealerCode
            this.editForm.historyActualCsfCode = res.data.historyActualCsfCode
            this.editForm.historyActualCsfCn = res.data.historyActualCsfCn
            this.editForm.historyActualAdmissionMonth =
              res.data.historyActualAdmissionMonth

            this.editForm.clusterCode = res.data.clusterCode
            this.editForm.cluster = res.data.cluster
            this.editForm.district = res.data.district
            this.editForm.rsmName = res.data.rsmName
            this.editForm.rsmCode = res.data.rsmCode
            this.editForm.provinceCN = res.data.provinceCN

            this.defaultDistributorCode = res.data.distributorCode
            this.defaultDistributorCn = res.data.distributorCn

            if (this.selectData.isLock) {
              // 显示锁定警告提示
              this.showAlertLock = true
              this.alertmsgLock = `${this.selectData.projectName || this.selectData.projectId}项目已锁定`

              if (this.role !== '1' && this.role !== '3' && !this.selectData.charge) {
                this.actualAccessDisabled = true
                this.actualAccessMethodDisabled = true
              }
            }

            if (!this.showEdit) {
              if (this.defaultActualAccessMethod === '正式准入') {
                if (this.role !== '1') {
                  this.dealerPersonnelDisabled = true
                  this.reverseReasonDisabled = true
                }
              } else {
                this.reverseReasonDisabled = true
                if (this.selectData.isConfirmed ||
                  (res.data.extendLastAdmission && res.data.createOrExtendFlag === 0 && res.data.parentIsConfirmed && res.data.hasRecord === 0)) {
                  if (this.role !== '1') {
                    this.actualAccessDisabled = true
                  }
                  if (!this.showEdit) {
                    this.showAlert1 = true
                    this.showAlert2 = true
                  }
                }
              }
            } else {
              if (this.selectData.isConfirmed ||
                (res.data.extendLastAdmission && res.data.createOrExtendFlag === 0 && res.data.parentIsConfirmed && res.data.hasRecord === 0)) {
                this.showAlert1 = true
              }
            }

            if (this.defaultActualAccessMethod === '') {
              if (this.role !== '1') {
                this.reverseReasonDisabled = true
              }
            }
            if (this.editForm.reverseDeclarationReason) {
              this.reverseReasonDisabled = false
            }

            if (!this.showEdit && this.editForm.actualAccessMethod) {
              this.formRules.actualAccessMethod[0].required = true
              this.formRules.actualAdmissionDate[0].required = true
              this.formRules.actualAdmissionDealer[0].required = true
            }

            this.fixedPickerOptions = {
              disabledDate(time) {
                const start = new Date(res.data.projectStartDate).getTime()
                const end = new Date(res.data.projectEndDate).getTime()
                return time.getTime() <= start || time.getTime() >= end
              },
            }

            if (!this.editForm.actualAdmissionDealerCode || !this.editForm.actualAdmissionDealer) {
              this.editForm.actualAdmissionDealerCode = ''
              this.editForm.actualAdmissionDealer = ''
            }
          }
          this.formLoding = false
        })
        .catch((error) => {
          this.formLoding = false
          console.log(error)
        })

    },
    getProductAdmissionListFilterOptions() {
      this.filterLoding = true
      if (this.distributorInfor.length > 0) {
        this.currentDistributorInfor = this.distributorInfor
        this.filterLoding = false
        if (this.editForm.actualAdmissionDealerCode) {
          this.recommended(this.editForm.actualAdmissionDealerCode)
        }
        return
      }
      this.$apiFetch
        .post(
          `/acc/productAdmission/getProductAdmissionListFilterOptions`,
          {},
        )
        .then((res) => {
          if (res && res.code == 200) {
            console.log(res.data)
            /*this.admissionStatusMap = new Map(
              Object.entries(res.data.admissionStatusMap || {}),
            )*/
            this.admissionStatusMap = res.data.admissionStatusMap
            this.recentSoStatusMap = new Map(
              Object.entries(res.data.recentSoStatusMap || {}),
            )
            this.recentSoTypeMap = new Map(
              Object.entries(res.data.recentSoTypeMap || {}),
            )
            this.reverseEntryReasonMap = new Map(
              Object.entries(res.data.reverseEntryReasonMap || {}),
            )
            this.soValidatedMap = new Map(
              Object.entries(res.data.soValidatedMap || {}),
            )
            this.commercialTerritory = res.data.commercialTerritory
            this.distributorInfor = res.data.distributorInfor
            this.currentDistributorInfor = res.data.distributorInfor
            this.projectSupportDealer = res.data.projectSupportDealer
            this.filterLoding = false

            if (this.editForm.actualAdmissionDealerCode) {
              this.recommended(this.editForm.actualAdmissionDealerCode)
            }
          }
        })
        .catch((error) => {
          this.filterLoding = false
          console.log(error)
        })
    },
    getHistoryChangeRecord() {
      if (this.showEdit) {
        this.tableLoading = true
        const queryParams = {
          productAdmissionId: this.selectData.id,
          projectId: this.selectData.projectId,
          terminalCode: this.selectData.terminalCode,
          productCode: this.selectData.productCode,
          currentPage: this.currentPage,
          pageSize: this.pageSize,
        }
        if (this.historyOrderBy) {
          queryParams['historyOrderBy'] = this.historyOrderBy
        }
        this.$apiFetch
          .post(`/acc/productAdmission/getHistoryChangeRecord`, queryParams)
          .then((res) => {
            if (res && res.code == 200) {
              console.log(res.data)
              this.changeRecordList = res.data.list
              this.total = res.data.total
              this.tableLoading = false
            }
          })
          .catch((error) => {
            this.tableLoading = false
            console.log(error)
          })
      }
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
      this.getHistoryChangeRecord()
    },
    handlePageChange(newPage) {
      this.currentPage = newPage
      this.getHistoryChangeRecord()
    },
    handleReverseFillReason() {
      // this.showAlert = this.reverseReasonDisabled
      this.alertmsg = '该条目不处于倒车状态，不可填写倒车原因'
    },
    verifyData(formName) {
      if (formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.commitData()
          }
        })
      }
    },
    commitData() {
      this.dialogVisibleReverseFillReason = false
      this.saveLoading = true
      const params = this.editForm
      console.log(params)
      this.$apiFetch
        .post(
          `/acc/productAdmission/updateProductAdmission?` +
          'userId=' +
          this.loginUser,
          params,
        )
        .then((res) => {
          console.log(res)
          this.saveLoading = false
          if (res && res.code == 200) {
            this.$message.success('提交成功')
            this.closeDialog(true, true)
          } else {
            this.$message.error(res.message || '提交失败')
          }
        })
        .catch((error) => {
          console.log(error)
          this.saveLoading = false
        })
    },
    renewBillOfLadingRanking(value, index, item) {
      if (this.oldProductAccessList.length != 0 && value != '是') {
        item.billOfLadingRanking =
          this.oldProductAccessList[index].billOfLadingRanking
      }
      this.$forceUpdate()
    },
    cascaderDisabled(accessState, index) {
      if (this.withoutRules) {
        return false
      }
      if (accessState == '未准入') {
        return true
      }
      if (this.oldProductAccessList.length != 0) {
        return (
          accessState == this.oldProductAccessList[index].accessState &&
          this.oldProductAccessList[index].actualAdmittanceTimeList != null &&
          this.oldProductAccessList[index].actualAdmittanceTimeList != ''
        )
      }
    },
    isDisabled(dropdownValue, disabledOptions) {
      if (disabledOptions == undefined) return false
      return disabledOptions.includes(dropdownValue)
    },
    compareTime(time, compareTime) {
      // console.log(time - compareTime); // 这里计算两个时间之间的毫秒差
      return time - compareTime > 0
    },
    getDateRangeForWeek(year, month, week) {
      // 创建一个日期对象，设置为指定年份和月份的第一天
      const firstDayOfMonth = new Date(year, month - 1, 1)

      // 获取第一天是星期几（0表示星期日，1表示星期一，以此类推）
      const firstDayOfWeek = firstDayOfMonth.getDay()

      // 计算第一周的日期范围
      let startDateFirstWeek = new Date(
        year,
        month - 1,
        1 - firstDayOfWeek + 1,
      )
      let endDateFirstWeek = new Date(year, month - 1, 7 - firstDayOfWeek + 1)

      if (firstDayOfWeek === 0) {
        // 计算第一周的日期范围
        startDateFirstWeek = new Date(year, month - 1, 1 - firstDayOfWeek - 6)
        endDateFirstWeek = new Date(year, month - 1, 7 - firstDayOfWeek - 6)
      }

      // 计算给定周的日期范围
      const startDateGivenWeek = new Date(startDateFirstWeek)
      startDateGivenWeek.setDate(
        startDateFirstWeek.getDate() + (week - 1) * 7,
      )
      const endDateGivenWeek = new Date(startDateGivenWeek)
      endDateGivenWeek.setDate(startDateGivenWeek.getDate() + 6)

      // 返回日期范围
      return {
        startDate: startDateGivenWeek,
        endDate: endDateGivenWeek,
      }
    },
    getYearMonthWeek() {
      this.monthYearWeekOptions.push({
        label: '2025年以前',
        value: '2025年以前',
      })
      //获取最近三年
      let nowDate = new Date()
      let date = {
        year: nowDate.getFullYear(),
        month: nowDate.getMonth() + 1,
        date: nowDate.getDate(),
      }

      let yearList = []
      let monthList = []
      for (let month = 1; month <= date.month; month++) {
        monthList.push(month < 10 ? '0' + month : month)
      }
      let weekList = ['第1周', '第2周', '第3周', '第4周']
      yearList.push(date.year)
      yearList.sort(function (a, b) {
        return b - a
      })

      let count = 1
      for (var i = 0; i < yearList.length; i++) {
        var param = []
        for (var j = 0; j < monthList.length; j++) {
          var week = []
          for (var k = 0; k < weekList.length + 1; k++) {
            const dateRange = this.getDateRangeForWeek(
              yearList[i],
              j + 1,
              k + 1,
            )
            let weekDate =
              weekList[k] +
              '(' +
              dateRange.startDate.toLocaleDateString() +
              '-' +
              dateRange.endDate.toLocaleDateString() +
              ')'
            if (k === 4) {
              let month = 0
              if (j === 11) {
                month = 1
              } else {
                month = j + 2
              }
              let nextMonth = yearList[i] + '-' + month + '-1 00:00:00'
              let nextMonthDate = new Date(nextMonth)

              if (this.compareTime(nextMonthDate, dateRange.endDate)) {
                weekDate =
                  '第5周(' +
                  dateRange.startDate.toLocaleDateString() +
                  '-' +
                  dateRange.endDate.toLocaleDateString() +
                  ')'
                week.push({label: weekDate, value: weekDate})
                count = count + 1
              }
            } else {
              week.push({label: weekDate, value: weekDate})
              count = count + 1
            }
          }
          param.push({
            label: monthList[j],
            value: monthList[j],
            children: week,
          })
        }
        this.monthYearWeekOptions.push({
          label: yearList[i] + '',
          value: yearList[i] + '',
          children: param,
        })
        this.monthYearWeekOptions.push({
          label: '暂无药事会',
          value: '暂无药事会',
        })
      }
    },
    getMonthOption() {
      let monthList = [
        '1月',
        '2月',
        '3月',
        '4月',
        '5月',
        '6月',
        '7月',
        '8月',
        '9月',
        '10月',
        '11月',
        '12月',
      ]
      let timeList = ['上半月', '下半月']
      let count = 1
      for (var i = 0; i < monthList.length; i++) {
        var param = []
        for (var j = 0; j < timeList.length; j++) {
          param.push({label: timeList[j], value: timeList[j]})
          count = count + 1
        }
        this.monthOptions.push({
          label: monthList[i],
          value: monthList[i],
          children: param,
        })
      }
    },
    getCurrentYearMonthsList() {
      const currentDate = new Date()
      const currentYear = currentDate.getFullYear()
      const currentMonth = currentDate.getMonth() + 1
      const monthsList = []

      for (let month = 1; month <= currentMonth; month++) {
        monthsList.push({
          label: month < 10 ? '0' + month : month,
          value: month < 10 ? '0' + month : month, // 如果月份小于10，前面添加一个'0'
        })
      }
      this.monthYearOptions.push({
        label: currentYear + '',
        value: currentYear + '',
        children: monthsList,
      })
      this.monthYearYshOptions.push({
        label: currentYear + '',
        value: currentYear + '',
        children: monthsList,
      })
      for (let month = currentMonth; month <= 12; month++) {
        let children = [
          {label: '上半月', value: '上半月'},
          {label: '下半月', value: '下半月'},
        ]

        this.futureMonth.push({
          children: children,
          label: month < 10 ? '0' + month : month,
          value: month < 10 ? '0' + month : month, // 如果月份小于10，前面添加一个'0'
        })
      }
    },
    getYearMonth() {
      //获取最近三年
      let nowDate = new Date()
      let date = {
        year: nowDate.getFullYear(),
        month: nowDate.getMonth() + 1,
        date: nowDate.getDate(),
      }

      let yearCount = 3
      let year = date.year
      let yearList = []
      let monthList = [
        '01',
        '02',
        '03',
        '04',
        '05',
        '06',
        '07',
        '08',
        '09',
        '10',
        '11',
        '12',
      ]
      // let timeList = ['上半月','下半月']
      while (yearCount > 1) {
        yearList.push(year)
        year = year + 1
        yearCount = yearCount - 1
      }
      yearList.sort(function (a, b) {
        return b - a
      })

      let count = 1
      for (var i = 0; i < yearList.length; i++) {
        var param = []
        for (var j = 0; j < monthList.length; j++) {
          param.push({
            id: monthList[j],
            label: monthList[j],
            value: monthList[j],
          })
          count = count + 1
        }
        this.monthYearOptions.push({
          id: yearList[i] + '',
          label: yearList[i] + '',
          value: yearList[i] + '',
          children: param,
        })
        this.monthYearYshOptions.push({
          id: yearList[i] + '',
          label: yearList[i] + '',
          value: yearList[i] + '',
          children: param,
        })
      }
      this.monthYearOptions.push({
        label: '暂无药事会',
        value: '暂无药事会',
      })

      console.log(this.monthYearOptions)
    },
    handleChange(value, index) {
      console.log('value', value)
      if (value != undefined) {
        console.log(value[value.length - 1].indexOf('('))
        console.log(value[value.length - 1].indexOf(')'))
        let timeRange = value[value.length - 1].substring(
          value[value.length - 1].indexOf('(') + 1,
          value[value.length - 1].indexOf(')'),
        )
        let timeRangeList = timeRange.split('-')
        console.log(moment(timeRangeList[0]))
        this.editForm.productAccessList[index].accessStartDate = moment(
          timeRangeList[0],
        )
        this.editForm.productAccessList[index].accessEndDate = moment(
          timeRangeList[1],
        )
      }
    },
    submitProduct(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (
            this.editForm.actualAccessMethod !==
            this.defaultActualAccessMethod
          ) {
            if (this.defaultActualAccessMethod === '正式准入') {
              this.dialogVisibleReverseFillReason = true
            } else if (
              this.defaultActualAccessMethod === '临采' &&
              (this.editForm.actualAccessMethod === 'Blank' ||
                this.editForm.actualAccessMethod === '' ||
                this.editForm.actualAccessMethod === null)
            ) {
              this.dialogVisibleReverseFillReason = true
            } else if (
              this.defaultActualAccessMethod === '批量临采' &&
              (this.editForm.actualAccessMethod === 'Blank' ||
                this.editForm.actualAccessMethod === '' ||
                this.editForm.actualAccessMethod === null)
            ) {
              this.dialogVisibleReverseFillReason = true
            } else {
              this.commitData()
            }
          } else {
            this.commitData()
          }
        } else {
          console.log('error submit!!')
          this.scrollToFirstErrorField()
          this.saveLoading = false
          return false
        }
      })
    },
    scrollToFirstErrorField() {
      this.$nextTick(() => {
        // 找到第一个错误的表单项
        const firstErrorField = document.querySelector('.el-form-item__error')
        if (firstErrorField) {
          // 找到对应的表单项容器
          const formItem = firstErrorField.closest('.el-form-item')
          if (formItem) {
            // 滚动到该元素
            formItem.scrollIntoView({
              behavior: 'smooth',
              block: 'center',
              inline: 'nearest',
            })
          }
        }
      })
    },
    closeAlert() {
      this.showAlert = false
    },
    closeDialog(shouldClose, shouldReload = false) {
      console.log(this.changeRecordList)

      this.$emit('close-dialog', shouldClose, shouldReload)
      // if (!this.order) {
      //   this.$baseEventBus.$emit('reload-router-view')
      //   this.$store.dispatch('tabsBar/delRoute', this.$route)
      //   if (this.from == 'WaitToBeDone') {
      //     console.log('WaitToBeDone', this.from)
      //     this.$parent.closeDialogHandler()
      //     return
      //   }
      //   this.$router.push({
      //     name: this.from,
      //     // params: {activityId: '2'},
      //   })
      // }
    },
    diffDealerSubmit() {
      this.editDistributor = true
      this.dialogVisibleDiffDealer = false
      this.editForm.distributorCn = this.currentActualCsf.distributorCN
      this.editForm.distributorCode = this.currentActualCsf.distributorCode
      this.editForm.projectSupportDealerGroup =
        this.currentActualCsf.dealerGroup
      this.editForm.comRegionCn = this.currentActualCsf.comRegionCN
      this.editForm.csfCn = this.currentActualCsf.csfCN
      this.editForm.csfCode = this.currentActualCsf.csfCode
      this.editForm.distributorProvince = this.tmpProvinceCN
      this.defaultDistributorCode = this.editForm.distributorCode
      this.defaultDistributorCn = this.editForm.distributorCn

    },
    diffDealerCancle() {
      this.dialogVisibleDiffDealer = false
      if (!this.editDistributor) {
        this.editForm.distributorCode = this.defaultDistributorCode
        this.editForm.distributorCn = this.defaultDistributorCn
      }
    },
    handleEstDtcDateChange(valueArray) {
      this.editForm.estDtcDate = valueArray.join('/')
    },
    handleBillTimeTypeChange(valueArray) {
      this.editForm.billTimeType = valueArray.join('/')
    },
    handleEstAdmissionTimeChange(valueArray) {
      this.editForm.estAdmissionTime = valueArray.join('/')
    },
    cancelReverseForm() {
      this.dialogVisibleReverseFillReason = false
      this.editForm.reverseDeclarationReason = ''
    },
    tableSortChange({column, prop, order}) {
      if (order) {
        this.historyOrderBy = order === 'ascending' ? 'ASC' : 'DESC'
      } else {
        this.historyOrderBy = null
      }
      this.currentPage = 1
      this.getHistoryChangeRecord()
    },
    handleCustomFilter(query) {
      console.log(query)
      if (query) {
        this.currentDistributorInfor = this.distributorInfor
        this.deliveryDealerOptions[0].label = '全部数据'
        this.canLoadMore = false
        // 自定义过滤逻辑，例如同时匹配label和value，且不区分大小写
        this.currentDistributorInfor = this.currentDistributorInfor.filter(item => {
          const lowerQuery = query.toLowerCase()
          return item.phCode.toLowerCase().includes(lowerQuery) ||
            item.dealerName.toLowerCase().includes(lowerQuery)
        })
      } else {
        this.currentDistributorInfor = this.distributorInfor
        this.deliveryDealerOptions[0].label = '全部数据'
        this.canLoadMore = false
      }
    },
    isOptionDisabled(label) {
      if (this.notAllowedReverseFlag === true && label === '临采') {
        return true
      }
      if (this.notAllowedReverseFlag === true && label === 'Blank') {
        return true
      }
      if (this.notAllowedReverseFlag === true && label === '批量临采') {
        return true
      }
      return false
    },
  },
}
</script>
<style lang="scss" scoped>
@keyframes rotate {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.rotating {
  vertical-align: middle;
  animation: rotate 2s linear infinite;
}

/** 步骤条*/
.step-container {
  .clearfix {
    clear: both;
  }

  ul {
    list-style: none;
    padding: 0;
    padding-bottom: 24px;
    margin-left: 12px;
  }

  .in-progress {
    background-color: rgb(0, 145, 223);
    transition: background-color 0.3s ease; /* 添加背景颜色过渡效果 */
  }

  .not-started {
    background-color: gray;
    transition: background-color 0.3s ease; /* 添加背景颜色过渡效果 */
  }

  li {
    cursor: pointer;
    float: left;
    width: 20%;
    height: 40px;
    margin-left: -12px;
    position: relative;
    list-style: none;
    box-sizing: border-box;
    font-size: 16px;
    font-weight: bold;
    transition: background-color 0.3s ease, transform 0.3s ease; /* 添加背景颜色和transform过渡效果 */
  }

  /* 鼠标悬停效果
li:hover {
background-color: transparent;
transform: scale(1.05, 1.05);
}*/

  .in-progress-less {
    border-color: transparent transparent transparent rgb(0, 145, 223);
  }

  .not-started-less {
    border-color: transparent transparent transparent gray;
  }

  .less {
    border-style: dashed dashed dashed solid;
    border-width: 20px 0 20px 20px;
    top: 0;
  }

  .less1 {
    border-style: dashed dashed dashed solid;
    border-color: transparent transparent transparent #fff;
    border-width: 20px 0 20px 20px;
    top: 0;
  }

  .right {
    right: 0;
  }

  .left {
    left: 0;
  }

  samp {
    display: block;
    position: absolute;
    z-index: 2;
  }

  span {
    display: block;
    position: relative;
    float: left;
    z-index: 3;
  }

  .block {
    height: 40px;
    line-height: 40px;
    text-align: center;
    vertical-align: middle;
    color: #fff;
    float: left;
    z-index: 1;
    padding: 0 10px;
    width: 80%;
  }
}

#button {
  padding-top: 20px;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.fileDetail {
  display: grid;
  grid-template-columns: 1fr 1fr;

  > div {
    padding: 8px 0;
    display: flex;
    flex-direction: column;
    font-size: 12px;
    line-height: 24px;
    height: 48px;
    color: rgb(16, 56, 79);
    word-break: normal;

    > label {
      display: block;
      min-width: 120px;
      font-size: 12px;
      color: rgb(170, 170, 170);
    }
  }

  .merge_two_cells {
    grid-column: 1 / span 2;
  }

  .tag-wrapper {
    display: flex;
  }
}

.el-tag + .el-tag {
  margin-left: 10px;
}

.button-new-tag {
  margin-left: 10px;
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}

.input-new-tag {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}

.el-select {
  width: 80%;
}

.select-with-tip {
  display: inline-flex;
  flex-direction: column; /* 使子元素垂直排列 */
  position: relative;
  width: 100%; /* 或者你需要的宽度 */
}

.select-tip {
  margin-top: 2px; /* 与输入框的间距 */
  font-size: 12px; /* 提示文字大小 */
  color: #00a6ff; /* 提示文字颜色 */
  line-height: 1;
  /* 可以根据需要添加更多样式，如背景色、边框等 */
  /* background-color: #f5f7fa; */
  /* padding: 4px 8px; */
  /* border-radius: 4px; */
}

.confirm {
  background-color: rgb(255, 49, 98) !important;
  color: #fff !important; /* 统一按钮文字颜色，通常设置为白色或对比色 */
}

/* 统一按钮悬停状态的样式 */
.confirm:hover {
  background-color: rgb(255, 49, 98) !important;
  border-color: #fff !important;
}

.el-form-item__label {
  white-space: pre; /* 保留空格序列 */
}

.custom-footer {
  padding-right: 20px;
}

/* 使用深度选择器隐藏前缀区域 */
::v-deep(.el-input__prefix) {
  display: none !important;
}

::v-deep(.el-form-item__content) {
  line-height: 32px !important;
}

.el-dialog__body {
  padding: 0px 0px;
}

::v-deep(.el-collapse-item__header) {
  font-size: 16px;
}

.custom-empty {
  text-align: center;
}

.no-data-text {
  color: #909399;
  font-size: 14px;
}

.load-more-btn {
  color: #409eff;
  font-size: 12px;
  padding: 6px 12px;
  width: 95%;
}

//::v-deep(.el-button) {
//   background-color: #ff3162 !important;
//   color: #fff !important;
//}
.select-mask-wrapper {
  position: relative;
  display: inline-block;
  width: 100%;
}

/* 遮罩层覆盖整个 select */
.click-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: transparent; /* 完全透明 */
  cursor: not-allowed;
  z-index: 10; /* 确保在 el-select 上方 */
}

/* 隐藏前缀图标容器并调整输入框位置 */

::v-deep(.el-input--prefix .el-input__inner) {
  padding-left: 12px; /* 可根据实际情况微调，使文字居中 */
}

::v-deep .el-input__inner::placeholder {
  color: #4c4f4f;
}

.alert-class {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 10px;
}

</style>
