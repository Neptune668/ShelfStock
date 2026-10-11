/**
 * 下拉选项构建工具
 */

/** 下拉选项 */
export interface SelectOption<T extends string = string> {
  value: T
  label: string
}

/** 根据「枚举值 → 中文文案」映射生成下拉选项数组 */
export function buildOptions<T extends string>(textMap: Record<T, string>): SelectOption<T>[] {
  return (Object.keys(textMap) as T[]).map((value) => ({ value, label: textMap[value] }))
}
