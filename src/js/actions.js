/////////////////////////  Navigation ////////////////////////////////////////////////////
export const NAV_ACTIVATE = 'NAV_ACTIVATE';

export function navActivate (active) {
  return { type: NAV_ACTIVATE, active: active};
}
