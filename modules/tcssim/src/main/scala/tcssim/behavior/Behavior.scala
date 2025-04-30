// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim.behavior

import tcssim.TcsEpicsDB

trait Behavior[F[_]] {
  def process(db: TcsEpicsDB[F]): F[Unit]
}
